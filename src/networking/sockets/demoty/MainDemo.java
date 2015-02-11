package networking.sockets.demoty;

import java.net.*;
import java.io.*;


// Allegro:          X-Job-Offer: Szukamy programistow, ktorzy zagladaja w takie miejsca. Wyslij do nas CV na adres generalist(at)allegro.pl, na pewno sie odezwiemy.
// Demotywatory:    <meta name="job-offer" content="Robisz w PHP, znasz MySQL, tworzysz UI z pomocą JS, szukasz pracy? Może właśnie ją znalazłeś! Ślij CV na praca@mmg.pl" />

public class MainDemo
{

    public static void main(String[] args) {

        getAllegroURLConnectionToFile();
    }

    public static void getURLConnection2(){
        try {
            URL hh = new URL("http://demotywatory.pl");
            URLConnection connection = hh.openConnection();
            String redirect = connection.getHeaderField("Location");
            if (redirect != null) {
                connection = new URL(redirect).openConnection();
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            System.out.println();
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void getDemotywatoryURLConnectionToFile(){
        try {
          //  URL hh = new URL("http://demotywatory.pl");

            for(int i = 2; i < 10; i++)
            {
                URL hh = new URL("http://demotywatory.pl/page/" + i);

                URLConnection connection = hh.openConnection();
                String redirect = connection.getHeaderField("Location");
                if (redirect != null) {
                    connection = new URL(redirect).openConnection();
                }
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                System.out.println();

                File file = new File("src/files/indexDemotyPage" + i + ".html");

                BufferedWriter bufferedWriter;

                bufferedWriter = new BufferedWriter(new FileWriter(file));

                String line;
                while ((line = br.readLine()) != null)
                {
                    System.out.println(line);
                    bufferedWriter.write(line + "\n");
                }
                bufferedWriter.flush();
            }



            /*while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
            }*/
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void getAllegroURLConnectionToFile(){
        try {
            //  URL hh = new URL("http://demotywatory.pl");


                URL hh = new URL("http://www.allegro.pl");

                URLConnection connection = hh.openConnection();
                String redirect = connection.getHeaderField("Location");
                if (redirect != null) {
                    connection = new URL(redirect).openConnection();
                }
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                System.out.println();

                File file = new File("src/files/indexAllegroMainPage.html");

                BufferedWriter bufferedWriter;

                bufferedWriter = new BufferedWriter(new FileWriter(file));

                String line;
                while ((line = br.readLine()) != null)
                {
                    System.out.println(line);
                    bufferedWriter.write(line + "\n");
                }
                bufferedWriter.flush();




            /*while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
            }*/
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void getURL(){
        int port = 80;
        URI dsa;

        try
        {
            // URL url = new URL("http://allegro.pl/index.php");
            URL url = new URL("http://demotywatory.pl/");

            if (url.getPort() != -1)
                port = url.getPort();

            if (!(url.getProtocol().equalsIgnoreCase("http")))
            {
                System.err.println("something else then http");

            }

            // Socket socket2 = new Socket();

            //String allegro = "allegro.pl";

            //URLConnection connection = hh.openConnection();
            //String redirect = connection.getHeaderField("Location");


            Socket socket = new Socket(url.getHost(), 80);
            OutputStream theOutput = socket.getOutputStream();
            // no auto-flushing
            PrintWriter pw = new PrintWriter(theOutput, false);
            // native line endings are uncertain so add them manually

            System.out.println(url.getFile());

            //pw.print("GET / HTTP/1.1\n\r\n");
            pw.println("GET /index.html HTTP/1.0");
            pw.println("Host: allegro.pl");
            pw.println("User-Agent: Mozilla/5.0 (Windows NT 6.2; WOW64; rv:35.0) Gecko/20100101 Firefox/35.0");
            pw.println("Accept: text/html");
            pw.println("Accept-Language: en-US,en;q=0.5");
            pw.println("Accept-Encoding: gzip, deflate");
            pw.println("Connection: keep-alive");


            // pw.print("");
            // pw.print("");

            // pw.print("Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            pw.print("\r\n");
            pw.flush();

            InputStream in = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(isr);

            // int c;
            // while ((c = br.read()) != -1)
            // {
            // System.out.print((char) c);
            // }

            File file = new File("src/files/index.html");

            BufferedWriter bufferedWriter;

            bufferedWriter = new BufferedWriter(new FileWriter(file));

            String line;
            while ((line = br.readLine()) != null)
            {
                System.out.println(line);
                bufferedWriter.write(line + "\n");
            }
            bufferedWriter.flush();

        }
        catch (IOException ex)
        {
            System.err.println(ex);
        }
    }

    public static void getURLConnection(){
        try {
            URL hh = new URL("http://demotywatory.pl");
            URLConnection connection = hh.openConnection();
            String redirect = connection.getHeaderField("Location");
            if (redirect != null) {
                connection = new URL(redirect).openConnection();
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            System.out.println();

            boolean body = false;

            while ((inputLine = in.readLine()) != null && body == false) {
                System.out.println(inputLine);

                String[] words = inputLine.split(" ");

                int wordsLength = words.length;

                for(int i = 0; i < wordsLength; i++)
                {
                    if(words[i].equals("<body"))
                    {
                        System.out.println("body found");
                        body = true;
                        break;
                    }

                    //System.out.println(words[i]);
                }

                //System.out.println("----------------------");
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }



}
