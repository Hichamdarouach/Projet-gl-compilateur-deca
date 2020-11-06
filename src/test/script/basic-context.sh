#! /bin/sh

# Auteur : gl23
# Version initiale : 01/01/2020

# Test minimaliste de la vérification contextuelle.
# Le principe et les limitations sont les mêmes que pour basic-synt.sh
cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"
PATH_VALID_PROVIDED=./src/test/deca/context/valid/provided/
PATH_INVALID_PROVIDED=./src/test/deca/context/invalid/provided/
PATH_VALID_NON_PROVIDED=./src/test/deca/context/valid/non_provided/
PATH_INVALID_NON_PROVIDED=./src/test/deca/context/invalid/non_provided/

test_context_invalide () {
    # $1 = premier argument.
    if test_context "$1" 2>&1 | grep -q -e "$1:[0-9][0-9]*:"
    then
        echo "[OK] Echec attendu pour test_context sur ${1/'./src/test/deca/context/invalid/'/ }."
    else
        echo "Succes inattendu de test_context sur ${1/'./src/test/deca/context/invalid/'/ }."
        exit 1
    fi
}    

test_context_valide () {
	if test_context "$1" 2>&1 | grep -q -e "$1:[0-9][0-9]*:"
	then
    	echo "Echec inattendu pour test_context sur ${1/'./src/test/deca/context/valid/'/ }."
    	exit 1
	else
    	echo "[OK] Succes attendu de test_context sur ${1/'./src/test/deca/context/valid/'/ }."
	fi
}

echo "########## BASIC CONTEXT ##########"
echo ""

# Test de validité contextuel sur tous les fichiers .deca valide fournis 
for cas_de_test in "$PATH_VALID_PROVIDED"*.deca
do
    test_context_valide "$cas_de_test"
done

# test de validité contextuel sur tous les fichier .deca valide non fournis 
for cas_de_test in "$PATH_VALID_NON_PROVIDED"*.deca
do
    test_context_valide "$cas_de_test"
done

# Test d'invalidité contextuel sur tous les fichiers .deca invalide fournis 
for cas_de_test in "$PATH_INVALID_PROVIDED"*.deca
do
    test_context_invalide "$cas_de_test"
done

# Test d'invalidité contextuel sur les tous fichier .deca invalide non fournis 
for cas_de_test in "$PATH_INVALID_NON_PROVIDED"*.deca
do
    test_context_invalide "$cas_de_test"
done
