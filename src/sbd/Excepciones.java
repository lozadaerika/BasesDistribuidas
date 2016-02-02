/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sbd;
import java.sql.SQLException;
/**
 *
 * @author Erika
 */
public class Excepciones {
    
    private static String mensajePersonalizado="";
    private static int codigo=0;
    public Excepciones()
    {
    
    }
    public static String GetMensajePersonalizado()
    {
    return mensajePersonalizado;
    }
    public static int GetCodigoError()
    {
    return codigo;
    }
    public static void GestionarExcepcion(SQLException excepcion)
    {
        codigo=excepcion.getErrorCode();
            ///-----------------------------------
            ///Capturar y personalizar los errores de base de datos
            ///-----------------------------------------------------
            String saltoLinea = "\n";
            //Mensaje personalizados para el usuario
            String problema = "EL PROBLEMA GENERADO PUEDE DEBERSE A LOS SIGUIENTES FACTORES: " + saltoLinea;
            String solucion = "POR FAVOR, PRUEBE LA SGUIENTE SOLUCION";
            String mensajeFinal = "NOTA: En caso de persistir el problema, llame a Soporte " +
                "Técnico" + saltoLinea + "o consulte con el Administrador del Sistema";
            String mensaje = null;
            switch (excepcion.getErrorCode())
            {
               
                case 2714: mensaje = problema +saltoLinea +" " + // Error conocido
                    "1.- La tabla ya existe." +
                    saltoLinea + saltoLinea +
                    solucion +
                    saltoLinea +
                    "1.- Utilice una tabla nueva" +
                    saltoLinea +
                    saltoLinea +
                    mensajeFinal;
                    break;
                    case 14016: mensaje = problema +saltoLinea +" " + // Error conocido
                    "1.- La publicación ya existe." +
                    saltoLinea + saltoLinea +
                    solucion +
                    saltoLinea +
                    "1.- Cree una publicación con otro nombre." +
                    saltoLinea +saltoLinea +
                    mensajeFinal;
                    break;
                    case 14058: mensaje = problema +saltoLinea +" " + // Error conocido
                    "1.- La suscripción ya existe." +
                    saltoLinea + saltoLinea +
                    solucion +
                    saltoLinea +
                    "1.- Cree una suscripcion diferente." +
                    saltoLinea + saltoLinea +
                    mensajeFinal;
                    break;
                    case 20026: mensaje = problema +saltoLinea +" " + // Error conocido
                    "1.- La publicacion no existe." +
                    saltoLinea + saltoLinea +
                    solucion +
                    saltoLinea +
                    "1.- Utilice una publicación que exista en la base." +
                    saltoLinea + saltoLinea +
                    mensajeFinal;
                    break;
                    case 21745: mensaje = problema +saltoLinea +" " + // Error conocido
                    "1.- Error en el filtro horizontal." +
                    saltoLinea +saltoLinea +
                    solucion +
                    saltoLinea +
                    "1.- Utilice un filtro correcto." +
                    saltoLinea + saltoLinea +
                    mensajeFinal;
                    break;
                default: mensaje =  // van a caer todos los errores no personalizados
                    "ERROR DESCONOCIDO: " +
                    saltoLinea + saltoLinea +
                    " MENSAJE: " + excepcion.getMessage() +
                     saltoLinea +
                     "CODIGO: " + excepcion.getErrorCode() +
                     saltoLinea +
                     "CAUSA: " + excepcion.getCause() +
                     saltoLinea +
                     "LINEA: " + excepcion.getStackTrace();
                    break;
            }
            //retornar el mensaje de error en un campo de la clase
            //para que sea el isiario de la clase el que decida en que control 
            //muestra el mensaje personalizado.
            mensajePersonalizado = mensaje;
    }
    
}
