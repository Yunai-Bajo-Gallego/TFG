import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author yunai
 */
////////////////////////////////////////
/*
DUDAS:

Si no mejora aunque proporcione otra alternativa igual. La doy o me quedo original?  ORIGINAL
Tanto en cada paso como en el resultado final

Rangos en orden aleatorio para cada mejorarSolucion o aleatorios una vez y aplicar ese orden a todas? CADA VEZ

Si hacemos 100 jobs + intentar mejorar puede irse a 2h. Con 5 son 8min. NO PROBLEM

*/
//////////////////////////////////////




//////////
/*
    Queda por hacer de código:

-ESTADISTICAS. PREPARAR MUCHO Y AUTOMATIZAR PARA EJECUTAR TODA LA NOCHE
*/
/////////
public class MejorarSoluciones {
    
    public final int alpha = Main.NUMERO_JOBS;
    public final int STOP_ITERACCIONES_SIN_MEJORAR = 3;
    
    public Solucion[] mejorarSoluciones(ArrayList<Solucion> soluciones,Rango[] rangos){
        
        Solucion[] resultado = new Solucion[soluciones.size()];//Al menos el mismo numero de salidas, 
        //si no se mejoran se queda igual. Si no o el mismo numero o alguna más si queremos devolver equivalentes. 
        //Preguntar. Si no, arraylist
        for (int i=0; i<soluciones.size(); i++) {      
            resultado[i] = mejorarSolucion(soluciones.get(i), rangos);
        }
        
        return resultado;
        
    }

    public Solucion mejorarSolucion(Solucion a,Rango[] rangos){
        
        Solucion solucion = new Solucion(a.getParametros(),a.getSolucion());
        Solucion solucionAnterior = new Solucion(a.getParametros(),a.getSolucion());
        Solucion solucionNueva = new Solucion(a.getParametros(),a.getSolucion());
        
        String[] temporalAumenta = new String[solucion.getParametros().length]; 
        String[] temporalDisminuye = new String[solucion.getParametros().length]; 
        
        List indices = new ArrayList();
        for (int j = GeneradorJobs.NUMEROS_PARAMETROS_EXTRA; j < GeneradorJobs.NUMEROS_PARAMETROS_EXTRA + rangos.length; j++) {
            indices.add(j);
        }   
        Collections.shuffle(indices);
        
        for (int i = GeneradorJobs.NUMEROS_PARAMETROS_EXTRA; i < GeneradorJobs.NUMEROS_PARAMETROS_EXTRA + rangos.length; i++) {

            int indice = (int) indices.remove(0);
            
            temporalAumenta = solucion.getParametros().clone();
            temporalDisminuye = solucion.getParametros().clone();
            int n = -1;
            boolean aumenta = false;           
            temporalAumenta[0] = solucion.getParametros()[0] + "-AUMENTA PARAM="+indice;//Cambiar la i por el orden que sea cuando el orden de tratar paramentros sea aleatorio
            temporalAumenta[indice] = siguienteValor(solucion.getParametros()[indice],rangos[indice-GeneradorJobs.NUMEROS_PARAMETROS_EXTRA]);
            temporalDisminuye[0] = solucion.getParametros()[0] + "-DISMINUYE PARAM="+indice;           
            temporalDisminuye[indice] = anteriorValor(solucion.getParametros()[indice],rangos[indice-GeneradorJobs.NUMEROS_PARAMETROS_EXTRA]);      

            //Ejecutamos jobs.
            //System.out.println("EJECUTAMOS: temporalAumenta: "+Arrays.toString(temporalAumenta));
            Solucion solucionAumenta = Main.ejecutaJob(temporalAumenta);
            //System.out.println("EJECUTAMOS: temporalDisminuye: "+Arrays.toString(temporalDisminuye));
            Solucion solucionDisminuye = Main.ejecutaJob(temporalDisminuye);
            
            
            //System.out.println("VALORES DE AUMENTA: "+solucionAumenta);
            //System.out.println("VALORES DE DISMINUYE: "+solucionDisminuye);
            n = compararSalidas(solucionDisminuye.getSolucion(),solucionAumenta.getSolucion(),Main.ELEMENTOS_SALIDA_MAXIMIZAR);//-1 param1 less, 0: eq, 1: param1 bigger 
            
            if(n==0){//Caso raro que sean iguales me quedo con uno al azar
                //System.out.println("SON IGUALES AUMENTA Y DISMINUYE PARAM="+indice);
                if(Math.random()>0.5){
                    solucion.setParametros(solucionAumenta.getParametros());
                    solucion.setSolucion(solucionAumenta.getSolucion());
                    aumenta = true;
                }
                else{
                    solucion.setParametros(solucionDisminuye.getParametros());
                    solucion.setSolucion(solucionDisminuye.getSolucion());
                }
            }
            else if(n==-1){
                //System.out.println("ES MEJOR 'AUMENTA'");
                solucion.setParametros(solucionAumenta.getParametros());
                solucion.setSolucion(solucionAumenta.getSolucion());
                aumenta = true;
            }
            else{
                //System.out.println("ES MEJOR 'DISMINUYE'");
                solucion.setParametros(solucionDisminuye.getParametros());
                solucion.setSolucion(solucionDisminuye.getSolucion());
            }

            n = compararSalidas(solucion.getSolucion(),solucionAnterior.getSolucion(),Main.ELEMENTOS_SALIDA_MAXIMIZAR);
            if(n<0){               
                //System.out.println("ES PEOR LA ACTUAL A LA ANTERIOR:");
                //System.out.println("ACTUAL: "+solucion);
                //System.out.println("ANTERIOR: "+solucionAnterior);
                solucion.setParametros(solucionAnterior.getParametros());
                solucion.setSolucion(solucionAnterior.getSolucion());
            }
            else if (n==1){
                //System.out.println("ES MEJOR LA ACTUAL A LA ANTERIOR:");
                //System.out.println("ACTUAL: "+solucion);
                //System.out.println("ANTERIOR: "+solucionAnterior);
                solucionAnterior.setParametros(solucion.getParametros());
                solucionAnterior.setSolucion(solucion.getSolucion());
            }
            
            solucionNueva.setParametros(solucion.getParametros());
            solucionNueva.setSolucion(solucion.getSolucion());
            
            int contador = 0;
            while(n>=0 && contador<STOP_ITERACCIONES_SIN_MEJORAR){
                if (aumenta){
                    solucionNueva.getParametros()[indice] = siguienteValor(solucionNueva.getParametros()[indice],rangos[indice-GeneradorJobs.NUMEROS_PARAMETROS_EXTRA]);
                }
                else{
                    solucionNueva.getParametros()[indice] = anteriorValor(solucionNueva.getParametros()[indice],rangos[indice-GeneradorJobs.NUMEROS_PARAMETROS_EXTRA]);
                }

                //System.out.println("SOLUCION       PARAMETROS: "+ Arrays.toString(solucion.getParametros()));
                //System.out.println("SOLUCION-NUEVA PARAMETROS: "+ Arrays.toString(solucionNueva.getParametros()));
                
                solucionNueva = Main.ejecutaJob(solucionNueva.getParametros());
                
                n = compararSalidas(solucionNueva.getSolucion(),solucion.getSolucion(),Main.ELEMENTOS_SALIDA_MAXIMIZAR);

                if(n==1){
                    //System.out.println("!!!!!!!SE HA MEJORADO LA SOLUCION AL AUMENTAR="+aumenta+"!!!!!!!!!!!");
                    //System.out.println("ACTUAL: "+solucion);
                    //System.out.println("NUEVA: "+solucionNueva);
                    solucion.setParametros(solucionNueva.getParametros());
                    solucion.setSolucion(solucionNueva.getSolucion());
                    contador = 0;
                }
                else{
                    contador++;
                }
            }  
        }
        if(a.getSolucion()!=solucion.getSolucion()){
            System.out.println("Se ha mejorado la solucion:");
            System.out.println("ENTRADA: "+Arrays.toString(a.getSolucion()));
            System.out.println("SALIDA: "+Arrays.toString(solucion.getSolucion()));
        }
        return solucion;
    }
    
    
    private String siguienteValor(String strValorActual,Rango r){
        double resultado = r.getMinValor();
        
        double longitud_rango = r.getMaxValor() -r.getMinValor();
        double particion = longitud_rango / alpha;
        double[] array_valores_aproximar = new double[alpha+1];
        for (int i = 0; i < array_valores_aproximar.length; i++) {
            array_valores_aproximar[i] = (particion*i)+ r.getMinValor();
        }
        int i=0;
        double valorActual = Double.valueOf(strValorActual);
        
        while(i<(array_valores_aproximar.length) && valorActual>=array_valores_aproximar[i]){
            i++;
        }
        if (i<array_valores_aproximar.length){
            resultado = array_valores_aproximar[i];   
        }
        //System.out.println("Entra a aumentar con valor: "+valorActual);
        //System.out.println("Valores: "+Arrays.toString(array_valores_aproximar));
        //System.out.println("Sale con valor: " + resultado);
        return String.valueOf(resultado);
    }   
    
    private String anteriorValor(String strValorActual,Rango r){
        double resultado = r.getMaxValor();
        
        double longitud_rango = r.getMaxValor() -r.getMinValor();
        double particion = longitud_rango / alpha;
        double[] array_valores_aproximar = new double[alpha+1];
        for (int i = 0; i < array_valores_aproximar.length; i++) {
            array_valores_aproximar[i] = (particion*i)+ r.getMinValor();
        }
        int i=0;
        double valorActual = Double.valueOf(strValorActual);
        while(i<array_valores_aproximar.length && valorActual>array_valores_aproximar[i] ){
            i++;
        }
        if (i<array_valores_aproximar.length){
            if(i==0){
                resultado = r.getMinValor();
            }
            else{
                resultado = array_valores_aproximar[i-1];  
            }
        }
        
        //System.out.println("Entra a disminuir con valor: "+valorActual);
        //System.out.println("Valores: "+Arrays.toString(array_valores_aproximar));
        //System.out.println("Sale con valor: " + resultado);
        return String.valueOf(resultado);
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
