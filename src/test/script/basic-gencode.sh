#! /bin/sh

# Auteur : gl23
# Version initiale : 01/01/2020

# Encore un test simpliste. On compile un fichier (cond0.deca), on
# lance ima dessus, et on compare le résultat avec la valeur attendue.

# Ce genre d'approche est bien sûr généralisable, en conservant le
# résultat attendu dans un fichier pour chaque fichier source.
cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:./src/main/bin:"$PATH"
PATH_VALID_PROVIDED=./src/test/deca/codegen/valid/provided/
PATH_VALID_NON_PROVIDED=./src/test/deca/codegen/valid/non_provided/

check_result() {
	if [ "$1" = "$2" ]
	then
		echo "[OK] Résultat attendu $3"
	else
		echo "Résultat innatendu de ima : $3"
		exit 1
	fi
}

test_codegen_valid() {
    # $1 = premier argument.
	#echo "arg = $1"
	file_name="${1/'./src/test/deca/codegen/valid/'/}"
	compil="${1/'.deca'/.ass}"
	result_file="${1/'.deca'/_result.txt}"
	rm -f "${compil}" 2>/dev/null
	decac "$1" || exit 1
	if [ -e "$compil" ]
	then
		resultat=$(ima "$compil" ) || echo "$resultat"
    	echo "[OK] Fichier ${file_name} compilé"
		rm -f "${compil}"
		attendu=`cat "${result_file}"`
		check_result "$resultat" "$attendu" "$file_name"
	else
		echo "Fichier "$compil" non généré."
		exit 1
	fi
}

echo "########## BASIC CODEGEN ##########"
echo ""

for cas_de_test in "$PATH_VALID_NON_PROVIDED"*.deca
do
    test_codegen_valid "$cas_de_test"
done

for cas_de_test in "$PATH_VALID_PROVIDED"*.deca
do
    test_codegen_valid "$cas_de_test"
done


# On ne teste qu'un fichier. Avec une boucle for appropriée, on
# pourrait faire bien mieux ...
#rm -f ./src/test/deca/codegen/valid/provided/cond0.ass 2>/dev/null
#decac ./src/test/deca/codegen/valid/provided/cond0.deca || exit 1
#if [ ! -f ./src/test/deca/codegen/valid/provided/cond0.ass ]; then
#    echo "Fichier cond0.ass non généré."
#    exit 1
#fi

#resultat=$(ima ./src/test/deca/codegen/valid/provided/cond0.ass) || exit 1
#rm -f ./src/test/deca/codegen/valid/provided/cond0.ass

# On code en dur la valeur attendue.
#attendu=ok

#if [ "$resultat" = "$attendu" ]; then
#    echo "Tout va bien"
#else
#    echo "Résultat inattendu de ima:"
#    echo "$resultat"
#    exit 1
#fi
