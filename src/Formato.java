import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Formato {

    private int checkSum;
    private double dato;
    private String salida;
    private String entrada;
    private int direccion;
    
    public Formato(){
        
        checkSum = 0x10;
        salida = ":10";
        direccion = 00;
        entrada = "";
    }
    
    public void direccion(){
        /*La dirección comienza en 0000. Esta se concatena a la cadena de salida y
        suma en la variable checkSum en hexadecimal. La dirección aumenta en 16
        hexadecimal cada vez que entra a la función*/
        salida += ("00" + Integer.toHexString(direccion));
        checkSum += Integer.parseInt(Integer.toHexString(direccion), 16);
        direccion += 16;
    }
    
    public void datos(boolean limite, int j){
        
        String dato;
        //Se compara si será la última fila del archivo. Además, se concatena en
        //la cadena de salida y se suma en checkSum.
        if(limite){
            salida = ":00000001";
            checkSum = 0x00000001;
            return;
        }
        /*Se leen 16 datos por línea. Estos se concatenan con la cadena final y son
        sumados en checkSum*/
        salida += "00";
        for(int i = 0; i < 32; i += 2){
            //Para no exceder la longitud de los datos de entrada.
            if(i + (32 * j) == entrada.length())
               break;
            //Los datos se toman de dos en dos.
            dato = entrada.substring(i + (32 * j), (i + (32 * j)) + 2);
            salida += dato;
            checkSum += Integer.parseInt(dato, 16);
        }
    }

    public void suma(){
        
        //Se obtiene el complemento a 2 de checkSum.
        String s = Integer.toHexString((~checkSum) + 1);
        //Solo se obtienen los dos últimos digitos.
        salida += s.substring(s.length() - 2, s.length());
        checkSum = 0x0;
    }
    
    public int archivoEntrada() throws IOException{
        
        String cadena;
        FileReader archivo = null;
        BufferedReader lector = null;
        /*Se lee el arhivo de entrada para obtener los datos*/
        try{
            archivo = new FileReader("entrada.txt");
            lector = new BufferedReader(archivo);
            while((cadena = lector.readLine()) != null){
                entrada += cadena;
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        lector.close();
        
        return entrada.length() / 32;
    }
    
    public void archivoSalida(PrintWriter escritor, FileWriter archivo){
        
        //La cadena de salida se guarda en el archivo y su valor se reinicia.
        try{
            escritor.println(salida);
        } catch (Exception e) {
            e.printStackTrace();
        }
        salida = ":10";
    }
    
    public static void main(String []args) throws IOException{
        
        Formato f = new Formato();
        int limite;
        FileWriter archivo = null;
        PrintWriter escritor = null;
        
        try{
            archivo = new FileWriter("formato.hex");
            escritor = new PrintWriter(archivo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        limite = f.archivoEntrada();
        for(int i = 0; i < limite - 1; i++){
            f.direccion();
            f.datos(false, i);
            f.suma();
            f.archivoSalida(escritor, archivo);
        }
        f.direccion();
        f.datos(true, limite - 1);
        f.suma();
        f.archivoSalida(escritor, archivo);
        
        archivo.close();
    }
}
