#! /bin/sh

# Auteur : gl23
# Version initiale : 01/01/2020

# Test minimaliste de la syntaxe.
# On lance test_synt sur un fichier valide, et les tests invalides.

# dans le cas du fichier valide, on teste seulement qu'il n'y a pas eu
# d'erreur. Il faudrait tester que l'arbre donné est bien le bon. Par
# exemple, en stoquant la valeur attendue quelque part, et en
# utilisant la commande unix "diff".
#
# Il faudrait aussi lancer ces tests sur tous les fichiers deca
# automatiquement. Un exemple d'automatisation est donné avec une
# boucle for sur les tests invalides, il faut aller encore plus loin.

cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"

# exemple de définition d'une fonction
test_synt_invalide () {
    # $1 = premier argument.
    if test_synt "$1" 2>&1 | grep -q -e "$1:[0-9][0-9]*:"
    then
        echo "[OK]Echec attendu pour test_synt sur ${1/'src/test/deca/syntax/invalid/'/ }."
    else
        echo "Succes inattendu de test_synt sur ${1/'src/test/deca/syntax/invalid/'/ }."
        exit 1
    fi
}    

test_synt_valide () {
	if test_synt "$1" 2>&1 | grep -q -e "$1:[0-9][0-9]*:"
	then
    	echo "Echec inattendu pour test_synt sur ${1/'src/test/deca/syntax/valid/'/ }."
    	exit 1
	else
    	echo "[OK]Succes attendu de test_synt sur ${1/'src/test/deca/syntax/valid/'/ }."
	fi
}

echo "########## BASIC SYNT ##########"
echo ""

# Test de validité sur tous les fichiers .deca valide fournis 
for cas_de_test in src/test/deca/syntax/valid/provided/*.deca
do
    test_synt_valide "$cas_de_test"
done

# test de validité sur tous les fichier .deca valide non fournis 
for cas_de_test in src/test/deca/syntax/valid/non_provided/*.deca
do
    test_synt_valide "$cas_de_test"
done

# Test d'invalidité sur tous les fichiers .deca invalide fournis 
for cas_de_test in src/test/deca/syntax/invalid/provided/*.deca
do
    test_synt_invalide "$cas_de_test"
done

# Test d'invalidité sur les tous fichier .deca invalide non fournis 
for cas_de_test in src/test/deca/syntax/invalid/non_provided/*.deca
do
    test_synt_invalide "$cas_de_test"
done
