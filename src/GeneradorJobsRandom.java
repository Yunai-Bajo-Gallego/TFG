/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author yunai
 */
public class GeneradorJobsRandom implements GeneradorJobs{

    //Entrada: El numeroParametros, suponemos que escribimos el correcto. Lo fijaremos en el "Main" como una constante.
    @Override
    public String[][] generarJobs(int numeroJobs,int numeroParametros, Rango[] rangoParametros){
            if (esEntradaValida(numeroParametros,rangoParametros)){
                String[][] resultado = new String[numeroJobs][numeroParametros+NUMEROS_PARAMETROS_EXTRA];
                for (int i=0; i<numeroJobs; i++){//Iteramos tantas veces como jobs queremos crear
                    //Le añadimos los parámetros fijos. Siempre son 3.
                    resultado[i][0] = "case-"+i+"-Random";//Id
                    resultado[i][1] = "0";
                    resultado[i][2] = "0";
                    
                    for (int j=NUMEROS_PARAMETROS_EXTRA; j<numeroParametros+NUMEROS_PARAMETROS_EXTRA; j++){//Iteramos tantas veces como parametros tenga el job.
  
                        double valorMinPosible = rangoParametros[j-NUMEROS_PARAMETROS_EXTRA].getMinValor();
                        double valorMaxPosible = rangoParametros[j-NUMEROS_PARAMETROS_EXTRA].getMaxValor();
                        
                        double valorParametroAleatorio = valorMinPosible + ((valorMaxPosible-valorMinPosible)*Math.random());
                        resultado[i][j] = String.valueOf(valorParametroAleatorio);
                    }
                }
                return resultado;
            }
            else{
                return null;
            }
    }
    
    //Comprobamos que los datos introducidos tienen sentido, por si nos equivocamos aunque se vayan a fijar estos al principio.
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
