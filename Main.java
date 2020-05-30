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
    private int content_length;
    static final String Root="../HTML";
    public ServerThread(Socket client)throws IOException{
        client_input=new BufferedReader(new InputStreamReader(client.getInputStream()));
        client_output = new PrintWriter(client.getOutputStream());
        content_length=0;
        for(String s=client_input.readLine();s!=null&&s.isEmpty()==false;s=client_input.readLine()){
            if(s.startsWith("GET")){
                System.out.println(s);
                method="GET";
                path=s.substring(4,s.indexOf("HTTP")-1);
            }
            if(s.startsWith("POST")){
                method="POST";
                System.out.println(s);
                path=s.substring(5,s.indexOf("HTTP")-1);
            }
            if(s.startsWith("Content-Length:")){
                content_length = Integer.parseInt(s.substring("Content-Length:".length()+1));
            }
        }
    }
    public void run(){
        try{
            if(method=="GET"){
                if(path.equals("/"))transfile(Root+"/main.html");
                else transfile(Root+path);
            }else{
                for(int i=0;i<content_length;++i){
                    System.out.print((char)client_input.read());
                }
                client_output.println("HTTP/1.1 200 OK");
                client_output.println();
                client_output.println("success");
                client_output.flush();
            }
            client_input.close();
            client_output.close();
        }catch(IOException E){
            E.printStackTrace();
        }
    }
    private void transfile(String filepath)throws IOException{
        if(new File(filepath).exists()){
            client_output.println("HTTP/1.1 200 OK");
            client_output.println();
            BufferedReader file=new BufferedReader(new FileReader(filepath));
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