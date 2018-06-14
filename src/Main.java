
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import jeplus.*;
import jeplus.agent.EPlusAgentLocal;
import jeplus.data.RVX;
import jeplus.postproc.ResultCollector;
import org.apache.log4j.PropertyConfigurator;



public class Main {

    final static int NUMERO_JOBS = 100;
    final static int NUMERO_PARAMETROS = 4;//Al job se le añadirán estos más:  id, index archivo datos, index arhivo temperaturas.
    //Tiene que coincidir el tamaño de este array con el de elementos de salida especificados en el ".imf"
    final static boolean[] ELEMENTOS_SALIDA_MAXIMIZAR = {true,true,true};
    
    final static String ruta =  "C:\\Users\\yunai\\Desktop\\TFG\\jEPlus_v1.5.2\\";
    final static String carpetaProyecto1 = "example_1-params_E+v8.1";
       
    final static Rango RANGO_1 = new Rango(0,360);
    final static Rango RANGO_2 = new Rango(0.008,0.025);
    final static Rango RANGO_3 = new Rango(0,0.10206170054859134);//No exactamente esto. pero sirve.
    final static Rango RANGO_4 = new Rango(0,20.286937833141963);     //-400*P3*P3+220*P3+2    
    final static Rango[] RANGOS_DE_LOS_PARAMETROS = {RANGO_1,RANGO_2,RANGO_3,RANGO_4};

    
    /**
     * @param args the command line arguments
     */
    
    public static void mostrarElementosAOptimizar(String ruta, String nombreArchivoIMF ,int x){
        File archivoIMF = new File(ruta+"\\"+nombreArchivoIMF+".imf");
        
        try {
            FileReader fr  = new FileReader(archivoIMF);
            char[] chars = new char[1000000];
            
            try {
                fr.read(chars);
            } catch (IOException ex) {
                System.out.println("Error al leer fichero .imf: "+ex);
            }
            String[] textoDividido = String.valueOf(chars).split("Output:Meter:MeterFileOnly,");
            if(textoDividido.length == x+1){
                System.out.println("Correcto el número de objetivos a optimizar especificados");
            }
            for (int i=1; i<textoDividido.length; i++) {
                String aux = textoDividido[i];
                aux = aux.substring(0, aux.indexOf("\r\n\r\n"));
                System.out.println("Variable a optimizar: " + aux);
            }
            
        } catch (FileNotFoundException ex) {
            System.out.println("Error al encontrar archivo \".imf\"");
        }
    }
    
    public static void main(String[] args) {
       File x = new File("RESULTADOS.csv");
       FileWriter fw = null;
        try {
             fw = new FileWriter(x);     
        } catch (IOException ex) {
        }
        
    //(creo meter:file:only)
    //Las variables a tener en cuenta para calcular las salidas y los elementos 
    //de esas salidas se dan por especificadas en el archivo .imf

for (int i=0;i<8;i++){        
        
      mostrarElementosAOptimizar(ruta+carpetaProyecto1,"HVACTemplate-5ZoneFanCoil",ELEMENTOS_SALIDA_MAXIMIZAR.length);
      
      // configure logger
      PropertyConfigurator.configure(ruta+"log4j.cfg");
 
      // load jEPlus configuration file
      JEPlusConfig.setDefaultInstance(new JEPlusConfig(ruta+"jeplus.cfg"));
 
      // load project file
      JEPlusProject Project = JEPlusProject.loadAsXML(new File(ruta+carpetaProyecto1+"\\project.jep")); // Or your own project file

      // create simulation manager
      EPlusBatch SimManager = new EPlusBatch (null, Project);
 
      // Set simulation agent
      SimManager.setAgent(new EPlusAgentLocal ( Project.getExecSettings()));
 
      // Validate project
      SimManager.validateProject();
 
      // If project is valid
      if (SimManager.getBatchInfo().isValidationSuccessful()) {
          
          GeneradorJobs generadorJobsSR = new GeneradorJobsSemiRandom();
          GeneradorJobs generadorJobsR = new GeneradorJobsRandom();
          String[][] jobsR = generadorJobsR.generarJobs(NUMERO_JOBS, NUMERO_PARAMETROS, RANGOS_DE_LOS_PARAMETROS);
          String[][] jobsSR = generadorJobsSR.generarJobs(NUMERO_JOBS, NUMERO_PARAMETROS, RANGOS_DE_LOS_PARAMETROS);
          
          RVX rvx = Project.getRVX(); // se llama a este método aunque sea un archivo rvi



          if (jobsR == null){
              System.out.println("Error al generar jobs. Entrada incorrecta");
          }
          else{//Entrada correcta
              
            System.out.println("Número de jobs: " +jobsR.length);
            System.out.println("Parámetros del job: "+ jobsR[0].length);
            // execute jobs
            SimManager.runJobSet(jobsR);

            // Alternatively, run jobs in the job list file 
            // SimManager.runJobSet(EPlusBatch.JobStringType.FILE, "example_3-RVX_v1.6_E+v8.3/jobs2.txt");

            // wait for jobs to finish
            try {
            do {
                Thread.sleep(2000);
            }while (SimManager.isSimulationRunning());
            }catch (InterruptedException iex) {
                SimManager.getAgent().setStopAgent(true);
            }

            ArrayList<ResultCollector> rcR = SimManager.getAgent().getResultCollectors();
            String directorioR = SimManager.getResolvedEnv().getParentDir(); // (Sirve para proyecto 1)             
            //String directorio = "C:\\Users\\yunai\\Desktop\\TFG\\jEPlus_v1.5.2\\example_2-rvx_E+v8.1\\output"; // (Error porque con rvx pone los resultados una carpeta más arriba de lo que debería y luego el result collectors no la encuentra)       

            // collect simulation results
  /*          
            HashMap<String, ArrayList<ArrayList<double []>>> Results = SimManager.getSimulationResults(
              rc,
              directorio,
              Project.getRvx(),
              null
            );
  */ 


          //Mio porque getRvx() no está en versión 1.5.2
          HashMap<String, ArrayList<ArrayList<double []>>> ResultsR = SimManager.getSimulationResults(
              rcR,
              directorioR,
              rvx,
              null
          );

            int n1 = ResultsR.size();
            System.out.println("Tamaño del mapa: " + n1);
            int n2 = ResultsR.entrySet().size();
            System.out.println("Salidas obtenidas (El doble de jobs porque cada job genera 2 arrays): " + n2*2);
            
            GestorSalidas gestorSalidasR = new GestorSalidasLocal();
            ArrayList<Solucion> solucionesR = gestorSalidasR.obtenerSoluciones(ResultsR, ELEMENTOS_SALIDA_MAXIMIZAR, jobsR);
            
            System.out.println("SOLUCIONES RANDOM SIN MEJORAR");
            System.out.println(solucionesR.size() + " soluciones obtenidas");
              try {
                  
                  //fw.write("SOLUCIONES RANDOM SIN MEJORAR (it."+ i + ") : "+ solucionesR.size() + " soluciones obtenidas \n");                 
                  for (Solucion aux : solucionesR) {
                      fw.write("R, "+aux.toString());
                      fw.write("\n");
                  }
                  fw.flush();

              } catch (IOException ex) {
                  Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
              }
            System.out.println(solucionesR);
            System.out.println("\n");
            System.out.println("A continuación, intentamos mejorar estas soluciones: ");
            
            //2ª parte, mejorar las soluciones obtenidas.
            MejorarSoluciones agenteR = new MejorarSoluciones();    
            Solucion [] resultadoR = new Solucion[solucionesR.size()];
            resultadoR = agenteR.mejorarSoluciones(solucionesR, RANGOS_DE_LOS_PARAMETROS);
            
            solucionesR = gestorSalidasR.obtenerSoluciones(resultadoR, ELEMENTOS_SALIDA_MAXIMIZAR);
            System.out.println("SOLUCIONES RANDOM MEJORADAS");
            System.out.println("----------------------\n\n\n"+solucionesR+"\n\n\n---------------------");     
            try {
               // fw.write("SOLUCIONES RANDOM MEJORANDO (it."+ i + ") : "+ solucionesR.size() + " soluciones obtenidas \n");
                for (Solucion aux : solucionesR) {
                      fw.write("RM, "+aux.toString());
                      fw.write("\n");
                }
                fw.flush();
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            
          }
            
            
            
            
            //TERMINADO PARTE RANDOM. HACEMOS PARTE SEMI-RANDOM
            
            
            
            
            
            
            
            
            
            
            
            
          if (jobsSR == null){
              System.out.println("Error al generar jobs. Entrada incorrecta");
          }
          else{//Entrada correcta
              
            System.out.println("Número de jobs: " +jobsSR.length);
            System.out.println("Parámetros del job: "+ jobsSR[0].length);
            // execute jobs
            SimManager.runJobSet(jobsSR);

            // Alternatively, run jobs in the job list file 
            // SimManager.runJobSet(EPlusBatch.JobStringType.FILE, "example_3-RVX_v1.6_E+v8.3/jobs2.txt");

            // wait for jobs to finish
            try {
            do {
                Thread.sleep(2000);
            }while (SimManager.isSimulationRunning());
            }catch (InterruptedException iex) {
                SimManager.getAgent().setStopAgent(true);
            }

            ArrayList<ResultCollector> rcSR = SimManager.getAgent().getResultCollectors();
            String directorioSR = SimManager.getResolvedEnv().getParentDir(); // (Sirve para proyecto 1)             
            //String directorio = "C:\\Users\\yunai\\Desktop\\TFG\\jEPlus_v1.5.2\\example_2-rvx_E+v8.1\\output"; // (Error porque con rvx pone los resultados una carpeta más arriba de lo que debería y luego el result collectors no la encuentra)       

            // collect simulation results
  /*          
            HashMap<String, ArrayList<ArrayList<double []>>> Results = SimManager.getSimulationResults(
              rc,
              directorio,
              Project.getRvx(),
              null
            );
  */ 


          //Mio porque getRvx() no está en versión 1.5.2
          HashMap<String, ArrayList<ArrayList<double []>>> ResultsSR = SimManager.getSimulationResults(
              rcSR,
              directorioSR,
              rvx,
              null
          );

            int n1SR = ResultsSR.size();
            System.out.println("Tamaño del mapa: " + n1SR);
            int n2SR = ResultsSR.entrySet().size();
            System.out.println("Salidas obtenidas (El doble de jobs porque cada job genera 2 arrays): " + n2SR*2);
            
            GestorSalidas gestorSalidasSR = new GestorSalidasLocal();
            ArrayList<Solucion> solucionesSR = gestorSalidasSR.obtenerSoluciones(ResultsSR, ELEMENTOS_SALIDA_MAXIMIZAR, jobsSR);
            
            System.out.println("SOLUCIONES SEMI-RANDOM SIN MEJORAR");
            System.out.println(solucionesSR.size() + " soluciones obtenidas");
            System.out.println(solucionesSR);

            
            try {
               //fw.write("SOLUCIONES SEMI-RANDOM SIN MEJORAR (it."+ i + ") : "+ solucionesSR.size() + " soluciones obtenidas \n");
               for (Solucion aux : solucionesSR) {
                      fw.write("SR, "+aux.toString());
                      fw.write("\n");
               }
               fw.flush();
           } catch (IOException ex) {
               Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
           }
            
            System.out.println("\n");
            System.out.println("A continuación, intentamos mejorar estas soluciones: ");
            
            //2ª parte, mejorar las soluciones obtenidas.
            MejorarSoluciones agenteSR = new MejorarSoluciones();    
            Solucion [] resultadoSR = new Solucion[solucionesSR.size()];
            resultadoSR = agenteSR.mejorarSoluciones(solucionesSR, RANGOS_DE_LOS_PARAMETROS);
            
            solucionesSR = gestorSalidasSR.obtenerSoluciones(resultadoSR, ELEMENTOS_SALIDA_MAXIMIZAR);
            System.out.println("SOLUCIONES SEMI-RANDOM MEJORADAS");
            System.out.println("----------------------\n\n\n"+solucionesSR+"\n\n\n---------------------");  
            
           try {
               //fw.write("SOLUCIONES SEMI-RANDOM MEJORADAS (it."+ i + ") : "+ solucionesSR.size() + " soluciones obtenidas \n");
               for (Solucion aux : solucionesSR) {
                      fw.write("SRM, "+aux.toString());
                      fw.write("\n");
                  }
               fw.flush();
           } catch (IOException ex) {
               Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
           }
            
            
//            
//            System.out.println("Versión de EnergyPlus: "+Project.getEPlusModelVersion());
          
          }//Fin entrada correcta
      }//Fin proyecto válido
      
      else {//Proyecto no válido
          System.out.println("Rama else. Proyecto no válido. Use la versión de JEplus adecuada y archivos rvi");
        //logger.info(SimManager.getBatchInfo().getValidationErrorsText());
      }
      
      
      
}

}
    public static Solucion ejecutaJob(String[] entrada){
      
      ArrayList<Solucion> resultado = null;
        
      PropertyConfigurator.configure(ruta+"log4j.cfg");
 
      // load jEPlus configuration file
      JEPlusConfig.setDefaultInstance(new JEPlusConfig(ruta+"jeplus.cfg"));
 
      // load project file
      JEPlusProject Project = JEPlusProject.loadAsXML(new File(ruta+carpetaProyecto1+"\\project.jep")); // Or your own project file

      // create simulation manager
      EPlusBatch SimManager = new EPlusBatch (null, Project);
 
      // Set simulation agent
      SimManager.setAgent(new EPlusAgentLocal ( Project.getExecSettings()));
 
      // Validate project
      SimManager.validateProject();
 
      // If project is valid
      if (SimManager.getBatchInfo().isValidationSuccessful()) {
          
          String[][] job = new String[1][entrada.length];
          job[0] = entrada;
          SimManager.runJobSet(job);

            try {
            do {
                Thread.sleep(2000);
            }while (SimManager.isSimulationRunning());
            }catch (InterruptedException iex) {
                SimManager.getAgent().setStopAgent(true);
            }

            ArrayList<ResultCollector> rc = SimManager.getAgent().getResultCollectors();
            String directorio = SimManager.getResolvedEnv().getParentDir(); // (Sirve para proyecto 1)             
            RVX rvx = Project.getRVX(); // se llama a este método aunque sea un archivo rvi

            HashMap<String, ArrayList<ArrayList<double []>>> Results = SimManager.getSimulationResults(
                rc,
                directorio,
                rvx,
                null
            );
            
            
            //double[] salida = Results.entrySet().iterator().next().getValue().get(0).get(0);
            
            GestorSalidas gestor = new GestorSalidasLocal();
            resultado = gestor.obtenerSoluciones(Results, ELEMENTOS_SALIDA_MAXIMIZAR, job);
            //Controlar. Salida debería tener solo una solucion. Si tiene 2 iguales quedarme con una al azar.
            
        }
        return resultado.get(0);
    }
    
    
}

