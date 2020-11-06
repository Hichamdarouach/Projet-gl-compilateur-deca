#! /bin/sh

# Auteur : gl23
# Version initiale : 01/01/2020

# Test minimaliste pour les etapes lex/synt/context
cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/:"$PATH"
PATH1=./src/test/script/

"$PATH1"basic-lex.sh

echo ""

"$PATH1"basic-synt.sh

echo ""

"$PATH1"basic-context.sh

echo ""

"$PATH1"basic-gencode.sh

echo ""
echo "Success !"
echo "" 
