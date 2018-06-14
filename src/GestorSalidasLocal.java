
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author yunai
 */

//El gestor de salidas local simplemente se queda con las soluciones óptimas. 
public class GestorSalidasLocal implements GestorSalidas{

    @Override
    public ArrayList<Solucion> obtenerSoluciones(HashMap<String, ArrayList<ArrayList<double[]>>> salidas,boolean[] maximizar_elementos_salida,String[][] jobs) {
            
        ArrayList<Solucion> resultado = new ArrayList<>();
        int index = 0;
        for (Map.Entry<String, ArrayList<ArrayList<double[]>>> entrySet : salidas.entrySet()) {
                
            ArrayList<ArrayList<double[]>> value = entrySet.getValue();
                
            for (int i = 0; i < value.size(); i++) {
                    for (int j = 0; j < value.get(i).size(); j++) { //Cada salida. value.get.get es el array entero.
                        if (resultado.isEmpty()){//Caso aún no hay solución
                            Solucion nueva = new Solucion(jobs[index],value.get(i).get(j));
                            resultado.add(nueva);
                        }
                        else{//Comparamos soluciones
                            int k=0;
                            int comparacion = 1;
                            while(k < resultado.size() && comparacion >-1) {//Si es peor se para. No se añade
                                comparacion = compararSalidas(value.get(i).get(j),resultado.get(k).getSolucion(),maximizar_elementos_salida);
                                if (comparacion == 1){// //Si es mejor se borra la actual pero no se añade aún.
                                    resultado.remove(k);
                                }
                                //Si la comparacion es 0 no se hace nada, pero se añadirá al final si ninguna vez es -1.
                                else{
                                    k++; 
                                }
                            }//fin while
                            if (comparacion>-1){
                                Solucion nueva = new Solucion(jobs[index],value.get(i).get(j));
                                resultado.add(nueva);
                            }
                        }//Fin else
                        
                    }//Fin 2º for
                }//Fin 1er for 
            index++;
            }
            return resultado;
        }
    
    
    @Override
    public ArrayList<Solucion> obtenerSoluciones(Solucion[] salidas,boolean[] maximizar_elementos_salida){
        
        ArrayList<Solucion> resultado = new ArrayList<>();
        
        for (int j = 0; j < salidas.length; j++) { //Cada salida. value.get.get es el array entero.
            if (resultado.isEmpty()){//Caso aún no hay solución
                Solucion nueva = new Solucion(salidas[j].getParametros(),salidas[j].getSolucion());
                resultado.add(nueva);
            }
            else{//Comparamos soluciones
                int k=0;
                int comparacion = 1;
                while(k < resultado.size() && comparacion >-1) {//Si es peor se para. No se añade
                    comparacion = compararSalidas(salidas[j].getSolucion(),resultado.get(k).getSolucion(),maximizar_elementos_salida);
                    if (comparacion == 1){// //Si es mejor se borra la actual pero no se añade aún.
                        resultado.remove(k);
                    }
                    //Si la comparacion es 0 no se hace nada, pero se añadirá al final si ninguna vez es -1.
                    else{
                        k++; 
                    }
                }//fin while
                if (comparacion>-1){
                    Solucion nueva = new Solucion(salidas[j].getParametros(),salidas[j].getSolucion());
                    resultado.add(nueva);
                }
            }//Fin else
        }
        return resultado;
    }
    
    
    public int compararSalidas(double[] valorPosible, double[] valorActual, boolean[] maximizar_elementos_salida){
        //Obviamente, los 3 arrays tendrán la misma longitud. no comprobar pero OJO.
        boolean mejora_todo = true;
        boolean empeora_todo = true;
        for (int i = 0; i < maximizar_elementos_salida.length; i++) {
            if (maximizar_elementos_salida[i]){//Caso este output se quiere maximizar
                if (valorPosible[i] > valorActual[i]){//Caso es mejor. gt y no gEt porque si es todo igual no se añade
                    empeora_todo = false;
                }//Fin caso es mejor
                else if (valorPosible[i] < valorActual[i]){//Caso es peor
                    mejora_todo = false;                   
                }//Fin caso es peor
            }
            else{//Caso este este output se quiere minimizar
                if (valorPosible[i] < valorActual[i]){//Caso es mejor
                    empeora_todo = false;
                }//Fin caso es mejor
                else if (valorPosible[i] > valorActual[i]){//Caso es peor
                    mejora_todo = false;
                }//Fin caso es peor
            }//Fin caso se quiere minimizar
        }

        if (empeora_todo){//Este delante porque sí los 2 booleans true, los 2 que se comparan son iguales y se descarta.
            return -1;
        }
        
        if (mejora_todo){
            return 1;
        }

        else{
            return 0;
        }
    }
    
}
    



