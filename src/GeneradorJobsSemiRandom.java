/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author yunai
 */
public class GeneradorJobsSemiRandom implements GeneradorJobs{

    @Override
    public String[][] generarJobs(int numeroJobs, int numeroParametros, Rango[] rangoParametros) {
        if (esEntradaValida(numeroParametros,rangoParametros)){
            String[][] resultado = new String[numeroJobs][numeroParametros+NUMEROS_PARAMETROS_EXTRA];
            
            for (int j=NUMEROS_PARAMETROS_EXTRA; j<numeroParametros+NUMEROS_PARAMETROS_EXTRA; j++){//Iteramos tantas veces como parametros tenga el job.
                double valorMinPosible = rangoParametros[j-NUMEROS_PARAMETROS_EXTRA].getMinValor();
                double valorMaxPosible = rangoParametros[j-NUMEROS_PARAMETROS_EXTRA].getMaxValor();        
                
                //El primer valor de los jobs es aleatorio. Luego serán semirandom
                double valorParametroAleatorio = valorMinPosible + ((valorMaxPosible-valorMinPosible)*Math.random());
                resultado[0][j] = String.valueOf(valorParametroAleatorio);    
                //Le añadimos los parámetros fijos. Siempre son 3.
                resultado[0][0] = "case-0-SemiRandom";//Id
                resultado[0][1] = "0";
                resultado[0][2] = "0";
                double sumaValoresTotales = valorParametroAleatorio;
                double media = sumaValoresTotales;
                double aux;       
                for (int i=1; i<numeroJobs; i++){//Iteramos tantas veces como jobs queremos crear.
                    
                    //Le añadimos los parámetros fijos. Siempre son 3.
                    resultado[i][0] = "case-"+i+"-SemiRandom";//Id
                    resultado[i][1] = "0";
                    resultado[i][2] = "0";
                    
                    //Empieza en 1 porque el 0 ya está fijado de forma aleatoria
                    aux = valorMinPosible + ((valorMaxPosible-valorMinPosible)*Math.random());
                    if (aux>=media){//Elegimos entre los valores media y maximo
                        valorParametroAleatorio = media + ((valorMaxPosible-media)*Math.random());
                    }
                    else{//Caso contrario elegimos entre los valores minimo y media.
                        valorParametroAleatorio = valorMinPosible + ((media-valorMinPosible)*Math.random());
                    }
                    sumaValoresTotales += valorParametroAleatorio;
                    media = (sumaValoresTotales)/(i+1);
                    resultado[i][j] = String.valueOf(valorParametroAleatorio);
                }
            }
            return resultado;       
        }
        else{
            return null;
        }
    }

    @Override
    public boolean esEntradaValida(int numeroParametros, Rango[] rangoParametros){
        if(numeroParametros == rangoParametros.length){
            return true;
        }
        else{
            return false;
        }
    }
    
}
