import com.fazecast.jSerialComm.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    static public void main(String[] Args) throws IOException {
        SerialPort port = SerialPort.getCommPort("COM1");
        port.setBaudRate(9600);
        port.setNumDataBits(8);
        port.setComPortTimeouts(port.TIMEOUT_READ_SEMI_BLOCKING, 1000, 0);
        port.setParity(port.NO_PARITY);
        port.setNumStopBits(port.ONE_STOP_BIT);
        BufferedInputStream in1;
        OutputStream out = port.getOutputStream();
        InputStream in = port.getInputStream();


        try{
            port.openPort();

            Scanner inC = new Scanner(System.in);


            //atd 3965
            while(true) {
                String str = "";
                str = inC.nextLine();
                //System.out.println(str);


                if(str.length()>5 && str.substring(0, 5).equals("call ")){
                    str = "atd" + str.substring(5) + "\r\n";
                }else{
                    str+="\r\n";
                }
                //System.out.println(str);

                out.flush();
                byte[] byteArr = str.getBytes(StandardCharsets.UTF_8);
                out.write(byteArr);
                out.flush();

                byte[] buffer = new byte[1024];
                int bytesRead;

            /*
            while ((bytesRead = in.read(buffer)) != -1){
                response.append((char) bytesRead);
            }*/

                String temp = "";
                while (true) {
                    StringBuilder response= new StringBuilder();
                    try (Reader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                        int c = 0;
                        while ((c = reader.read()) != -1) {
                            response.append((char) c);
                        }
                    } catch (Exception e) {
                    }
                    if (!(temp.equals(response.toString()))) {
                        System.out.println(response.toString());
                        if(response.toString().equals("RING")){
                            System.out.println("detected");
                            out.flush();
                            String str1 = "ata\r\n";
                            byte[] byteArr1 = str1.getBytes(StandardCharsets.UTF_8);
                            out.write(byteArr1);
                            out.flush();
                        }
                    }

                    temp = response.toString();
                    if (inC.hasNextLine()) break;
                }
                //System.out.println("end");
            }

        }catch (Exception e){
            e.printStackTrace();
        } finally {
            port.closePort();
            in.close();
            out.close();
            //System.out.println(response.toString());
        }

    }
}
