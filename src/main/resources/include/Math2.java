// Grenoble INP - Ensimag projet GL -*- mode: java -*-
// Library for class Math of Deca, coded in Deca

public class Math2 {
    // Plus grand floattant représentable en Déca
    public float MAX_VAL = 2147483647;

    // Plus petit floattant représentable en Déca
    public float MIN_VAL = power(2,-149);

    // Le flottant le plus proche de PI
    public float PI = 3.141592653f;



    // Calcule la puissance d'un flottant par un entier
    public float power(float a,int b){
      // La convention x^0 = 1
      if (b == 0) {return 1;}
      // On applique la régle x^(-a) = (1/x)^a
      if (b<0) {return power(a,b+1)/a;}
      // On utiliser la méthode de l'exponentiation rapide qui est de complexité log(b)
      if ((b / 2)*2 == b){return power(a*a,b/2);}
      return a * power(a*a,(b-1)/2);
    }



    // Fonction qui calcule le floattant le plus proche de la racine d'un flottant
    // En utilisant la méthode de Héron
    public float sqrt(float x){
      float a = 1;
      float b = (a + x/a)/2;
      float tmp;
      while (a != b){
        tmp = b;
        a = b;
        b =  (tmp + x/tmp)/2;
      }
      return a;
    }

    //Calcule la distance avec le flottant les plus proche utiliser pour le calcul de précision
    public float ulp(float f) {
        int res = -128;
        // Le 0 est un cas particulier;
        if (f == 0){return power(2.0f,-149);}
        // La symétrie de la foction ulp
        if (f < 0) {return ulp(-f);}
        // Les 2 cas particuliers si f est trop grand ou f est trop petit
        if (f > MAX_VAL) {return MAX_VAL;}
        if (f < MIN_VAL) {return MIN_VAL;}
        while (f - power(2,res) >0){res = res + 1; }
        return power(2,res-24);
    }


    // float ulp(float f) {
    //     if (f < 0) {return ulp(-f);}
    //     int res = -128;
    //     while (f - power(2,res) > 0){res = res + 1; }
    //     return power(2,res-23);
    // }

    // retourne le signe d'un flottant
    public float signe(float f){
      if (f>=0){return 1.0f;}
      return -1.0f;
    }

    // floattant les plus proche de PI
    public float PI(){
      float b = 3.141592653f;
      return b;
    }


    // Premiere correction de PI
    public float reste_PI(){
      return -8.74227801f*power(10.0f,-8) ;
    }


    // Deuxieme correction de PI
    public float reste_PI1(){
      return 3.5527136788005f*power(10.0f,-15) ;
    }

    // retourne la valeur entiere superieure
    public int floor(float x){
      int i = 0;
      while (i <= x){
        i = i + 1;
      }
      return i;
    }

    // Fonction de réduction de l'intervalle avec correction
    public float reduce(float f){
      int n = floor(f/(PI()));
      return power(-1.0f,n)*((f - n*PI())-n*reste_PI() - n * reste_PI1());

    }

    // Calcule la factorielle d'un entier
    public float fact(int n){
      int i = 2;
      float res = 1;
      while (i <= n){
        res = res * i;
        i = i + 1;
      }
      return res;
    }


    // Definition de la fonction sinus
    public float sin(float f){
        float res ;
        int ordre ;
        float tmp ;

        // Parité de la fonction de sinus (impair)
        if (f < 0){return -sin(-f);}
        if (f > PI()){
          return sin(reduce(f));
        }

        // reduction de l'intervalle de [0,PI] à [0,PI/2]
        if (PI()/2 < f && f <= PI()) {return - sin((( -PI()  + f)-reste_PI())-reste_PI1());}

        res = f;
        ordre = 1;
        tmp = -power(f,3)/6;
        // Condition d'arret : quand les coefficient de Taylor ne sont plus sinificatifs
        while ( signe(tmp)*tmp > ulp(res) ){
          res = res  + tmp;
          ordre = ordre + 1;
          tmp = power(f,2*ordre + 1)*power(-1.0f,ordre) / fact(2 * ordre + 1);
        }
        return res;
      }


    // Profite de la definition de sin pour definir le cos
    public float cos(float f){
          if (f < 0){return cos(-f);}
          return sin(PI()/2 - f + reste_PI()/2);
      }



    public float atan(float f){
          float res = f;
          int ordre = 1;
          float tmp = -power(f,3)/3;

          //Cas particulier f = 1;
          if (f == 1.0f){return PI()/4;}
          if (f == 0){return 0;}
          // Parité de la fonction de arctan (impair)
          if (f < 0){return -atan(-f);}
          if (f > 1){return PI()/2 - atan(1/f);}
          // Condition d'arret : quand les coefficient de Taylor ne sont plus sinificatifs
          while ( signe(tmp)*tmp > ulp(res) ){
            res = res  + tmp;
            ordre = ordre + 1;
            tmp = power(f,2*ordre + 1)*power(-1.0f,ordre) / (2 * ordre + 1);
          }
          return res;
        }

    // Profite de la definition de arctan pour definir le arcsin
    public float asin(float f) {
        if (f >= 1.0f) {return PI()/2;}
        if (f < 0){return -asin(-f);}
        return 2*atan(f / (1+sqrt(1-power(f,2))));
    }

    // Profite de la definition de arctan pour definir le arcsin
    public float acos(float f) {
        if (f == 0) {return PI()/2;}
        if (f == 1) {return 0;}
        if (f < 0 ) {return PI() + reste_PI() - acos(-f);}
        return 2*atan(sqrt(1-power(f,2)) / (1+f) );
    }



}


// End of Deca Math library
