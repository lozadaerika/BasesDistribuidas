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
               case 102: mensaje = problema +saltoLinea +" " + // Error conocido
                    "1.- Error de Sintaxis." +
                    saltoLinea + saltoLinea +
                    solucion +
                    saltoLinea +
                    "1.- Revise el codigo Transact-SQL" +
                    saltoLinea +
                    saltoLinea +
                    mensajeFinal;
                    break;
                   case 1801: mensaje = problema +saltoLinea +" " + // Error conocido
                    "1.- Ya existe la base." +
                    saltoLinea + saltoLinea +
                    solucion +
                    saltoLinea +
                    "1.- Utilice una base que no exista" +
                    saltoLinea +
                    saltoLinea +
                    mensajeFinal;
                    break;
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
                    case 14055: mensaje = problema +saltoLinea +" " + // Error conocido
                    "1.- La publicación no existe no se puede eliminar." +
                    saltoLinea + saltoLinea +
                    solucion +
                    saltoLinea +
                    "1.- Elimine una publicacion existente." +
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
                    case 15004: mensaje = problema +saltoLinea +" " + // Error conocido
                    "1.- No se puede eliminar la suscripcion." +
                    saltoLinea + saltoLinea +
                    solucion +
                    saltoLinea +
                    "1.- Intente nuevamente.\n Verifique conexiones de la suscripcion" +
                    saltoLinea + saltoLinea +
                    mensajeFinal;
                    break;
                    case 18483: mensaje = problema +saltoLinea +" " + // Error conocido
                    "1.- Error en el acceso con sa." +
                    saltoLinea + saltoLinea +
                    solucion +
                    saltoLinea +
                    "1.- Verifique los datos." +
                    saltoLinea + saltoLinea +
                    mensajeFinal;
                    break;
                    case 20021: mensaje = problema +saltoLinea +" " + // Error conocido
                    "1.- No se puede encontrar la suscripción merge." 
+
                    saltoLinea + saltoLinea +
                    solucion +
                    saltoLinea +
                    "1.- Utilice otra suscripcion merge." +
                    saltoLinea + saltoLinea +
                    mensajeFinal;
                    break;
                    case 20025: mensaje = problema +saltoLinea +" " + // Error conocido
                    "1.- El nombre de una publicacion debe ser exclusivo aunque sea de otro tipo." +
                    saltoLinea + saltoLinea +
                    solucion +
                    saltoLinea +
                    "1.- Utilice otro nombre para la pubicación." +
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
                    case 20804: mensaje = problema +saltoLinea +" " + // Error conocido
                    "1.- Un articulo solo se puede utilizar en una replicacion Peer To Peer." +
                    saltoLinea + saltoLinea +
                    solucion +
                    saltoLinea +
                    "1.- Utilice otro articulo para la replicacion Peer To Peer." +
                    saltoLinea + saltoLinea +
                    mensajeFinal;
                    break;
                    case 21266: mensaje = problema +saltoLinea +" " + // Error conocido
                    "1.- No se puede utilizar la misma base para publicaciones de tipos diferentes." +
                    saltoLinea +saltoLinea +
                    solucion +
                    saltoLinea +
                    "1.- Utilice otra base de datos u otro tipo de publicación." +
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
    public static void GestionarExcepcion(Exception excepcion)
        {
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
            if(excepcion.getMessage().contains("No se pudo realizar la conexión TCI/IP"))
            {
                mensaje = problema +saltoLinea +" " + 
                    "1.- El servidor no se encuentra conectado o es incorrecto." +
                    saltoLinea +saltoLinea +
                    solucion +
                    saltoLinea +
                    "1.- Realice la accion con un servidor disponible." +
                    saltoLinea + saltoLinea +
                    mensajeFinal;
            }
            ///retornar el mensaje de error en un campo de la clase
            ///para que sea el isiario de la clase el que decida en que control 
            //muestra el mensaje personalizado.
            mensajePersonalizado = mensaje;
        }
    
}
