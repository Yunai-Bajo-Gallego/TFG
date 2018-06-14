
import java.util.Arrays;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author yunai
 */
public class Solucion {
    
    private String[] parametros;//Esto simplemente es el job con los parámetros de entrada.
    private double[] solucion; // Es double al igual que indica la salida automática de JEplus. 
                              //Si los valores de salida fuesen discretos esto se debería cambiarse por String o asignar
                             //un valor numérico a cada discreto.

    public Solucion(String[] parametros, double[] solucion) {
        this.parametros = new String[parametros.length];
        this.parametros = parametros.clone();
        this.solucion = new double[solucion.length];
        this.solucion = solucion.clone();
    }

    public String[] getParametros() {
        return parametros;
    }

    public void setParametros(String[] parametros) {
        this.parametros = parametros.clone();
    }

    public double[] getSolucion() {
        return solucion;
    }

    public void setSolucion(double[] solucion) {
        this.solucion = solucion.clone();
    }

    @Override
    public String toString() {
        return Arrays.toString(parametros) + "," + Arrays.toString(solucion) ;
    }
    
    
    
    
}
