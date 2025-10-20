package pt.isec.pd.exer16;

import java.net.*;

public class UsingHttpURLConnection {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Syntax: java UsingHttpURLConnection <URL>");
            return;
        }

        try {
            URL url = new URI(args[0]).toURL();
            URLConnection connection = url.openConnection();

            if (connection instanceof HttpURLConnection) {
                HttpURLConnection hConnection;

                hConnection = (HttpURLConnection) connection;

                hConnection.setFollowRedirects(false);

                hConnection.connect();

                if (hConnection.usingProxy())
                    System.out.println("Proxy server used");
                else
                    System.out.println("No proxy server used");

                String msg = hConnection.getResponseMessage();

                int code = hConnection.getResponseCode();

                if (code == HttpURLConnection.HTTP_OK)
                    System.out.println("Normal response returned : " + code + " " + msg);
                else
                    System.out.println("Abnormal response returned : " + code + " " + msg);

                System.out.println("Hit enter to continue");
                System.in.read();
            } else {
                System.err.println("Not an HTTP connection");
            }
        } catch (URISyntaxException e) {
            System.err.println("Invalid URL: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("I/O Error: " + e.getMessage());
        }
    }
}
