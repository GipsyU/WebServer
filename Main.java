import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main{
    public static void main(String[] args){
        try{
            ServerSocket server=new ServerSocket(80);
            while(true){
                Socket client=server.accept();
                new ServerThread(client).run();
            }
        }catch(IOException E){
			E.printStackTrace();
        }
    }
}
class ServerThread extends Thread {
    private BufferedReader client_input;
    private PrintWriter client_output;
    private String method,path;
    static final String Root="../HTML";
    public ServerThread(Socket client)throws IOException{
        client_input=new BufferedReader(new InputStreamReader(client.getInputStream()));
        client_output = new PrintWriter(client.getOutputStream());
        for(String s=client_input.readLine();!s.isEmpty();s=client_input.readLine()){
            if(s.startsWith("GET")){
                System.out.println(s);
                method="GET";
                path=s.substring(4,s.indexOf("HTTP")-1);
            }else if(s.startsWith("POST")){
                method="POST";
                path=s.substring(5,s.indexOf("HTTP")-1);
            }
        }
    }
    public void run(){
        try{
            if(path.equals("/"))transfile(Root+"/main.html");
            else transfile(Root+path);
            client_input.close();
            client_output.close();
        }catch(IOException E){
            E.printStackTrace();
        }
    }
    private void transfile(String filepath)throws IOException{
        if(new File(filepath).exists()){
            BufferedReader file=new BufferedReader(new FileReader(filepath));
            client_output.println("HTTP/1.1 200 OK");
            client_output.println();
            for(String s=file.readLine();s!=null;s=file.readLine()){
                client_output.println(s);
            }
            client_output.flush();
            file.close();
        }else{
            client_output.println("HTTP/1.1 404 Not Found");
        }
    }
}