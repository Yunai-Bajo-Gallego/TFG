/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author yunai
 */
public interface GeneradorJobs {
    
     public final int NUMEROS_PARAMETROS_EXTRA = 3; //Son: id-index archivo 1-index archivo 2. IMPORTANTE:SIEMPRE 3.
    
     public String[][] generarJobs(int numeroJobs,int numeroParametros, Rango[] rangoParametros);
     public boolean esEntradaValida(int numeroParametros, Rango[] rangoParametros);
    
}
