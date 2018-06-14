
import java.util.ArrayList;
import java.util.HashMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author yunai
 */
public interface GestorSalidas {
    
    public ArrayList<Solucion> obtenerSoluciones( HashMap<String, ArrayList<ArrayList<double []>>> salidas,boolean[] maximizar_elementos_salida,String[][] jobs);
    
    public ArrayList<Solucion> obtenerSoluciones(Solucion[] salidas,boolean[] maximizar_elementos_salida);

}
