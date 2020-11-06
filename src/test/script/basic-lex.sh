#! /bin/sh

# Auteur : gl23
# Version initiale : 01/01/2020

# Base pour un script de test de la lexicographie.
# On teste un fichier valide et un fichier invalide.
# Il est conseillé de garder ce fichier tel quel, et de créer de
# nouveaux scripts (en s'inspirant si besoin de ceux fournis).

# Il faudrait améliorer ce script pour qu'il puisse lancer test_lex
# sur un grand nombre de fichiers à la suite.

# On se place dans le répertoire du projet (quel que soit le
# répertoire d'où est lancé le script) :
cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"

echo "########### BASIC LEX ##########"
echo ""

# /!\ test valide lexicalement, mais invalide pour l'étape A.
# test_lex peut au choix afficher les messages sur la sortie standard
# (1) ou sortie d'erreur (2). On redirige la sortie d'erreur sur la
# sortie standard pour accepter les deux (2>&1)
if test_lex src/test/deca/syntax/invalid/provided/simple_lex.deca 2>&1 \
    | head -n 1 | grep -q 'simple_lex.deca:[0-9]'
then
    echo "Echec inattendu de test_lex sur simple_lex.deca."
    exit 1
else
    echo "[OK] simple_lex.deca."
fi

# Ligne 10 codée en dur. Il faudrait stocker ça quelque part ...
if test_lex src/test/deca/syntax/invalid/provided/chaine_incomplete.deca 2>&1 \
    | grep -q -e 'chaine_incomplete.deca:10:'
then
    echo "[OK] Echec attendu pour test_lex sur chaine_incomplète.deca."
else
    echo "Erreur non detectee par test_lex pour chaine_incomplete.deca"
    exit 1
fi

# Tous les fichier valid syntaxiquement doivent aussi l'être
# lexicalement
for deca_file in src/test/deca/syntax/valid/non_provided/*
do 
	if test_lex $deca_file 2>&1 | head -n 1 | grep -q $deca_file'.deca:[0-9]'
	then 
		echo "Echec inattendu de test_lex sur ${deca_file/'src/test/deca/syntax/valid/non_provided/'/ }."
		exit 1
	else
		echo "[OK] ${deca_file/'src/test/deca/syntax/valid/non_provided/'/ }."
	fi
done

# Test valide lexicalement mais pas syntaxiquement pour l'étape A
if test_lex src/test/deca/syntax/invalid/non_provided/bad_assign.deca 2>&1 \
	| head -n 1 | grep -q 'bad_assign.deca:[0-9]'
then
    echo "Echec inattendu de test_lex sur bad_assign.deca."
		exit 1
	else
		echo "[OK] bad_assign.deca."
fi
