package apartado1;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Scanner;

/**
 * @author N
 *
 */
public class Cliente {

	public static void main(String[] args) {

		Socket clientSocket = null; //socket cliente
		DataOutputStream dataOutputStream = null; //stream salida
		DataInputStream dataInputStream = null; //stream entrada
		BufferedReader entrada = null; //buffer de entrada
		int valorEntradaCliente;

		try {
			clientSocket = new Socket("localhost", 2000);// me conecto al servidor con puerto 2000
			dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());// para mandar al servidor
			dataInputStream = new DataInputStream(clientSocket.getInputStream());// para lo que devuelve el servidor

			while (true) {//mientras no acierte sigo pidiendo números
				
				System.out.println("Introduzca un número del 0  al 100");
				entrada = new BufferedReader(new InputStreamReader(System.in)); //buffer para leer datos introducidos
				
				valorEntradaCliente = Integer.parseInt(entrada.readLine());	//leer lo que introduce el cliente
				
				System.out.println("Ha introducido " + valorEntradaCliente + " enviando al servidor, espere ....");
				
				dataOutputStream.writeInt(valorEntradaCliente);//envío el número al servidor
				
				String mensajeServidor = dataInputStream.readUTF();//guardo lo que recibo del servidor
				System.out.println(mensajeServidor);// imprimo lo que me devuelve el servidor
				if(mensajeServidor.contains("Felicidades")) {
					break;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {//cerrando socket y streams
			try {
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				dataInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				dataOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
