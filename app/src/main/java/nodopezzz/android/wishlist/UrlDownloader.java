package nodopezzz.android.wishlist;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UrlDownloader {

    public static byte[] getResponseByte(String url) throws IOException{
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        try {

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                throw new IOException(connection.getResponseMessage());
            }

            InputStream input = connection.getInputStream();
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            int bufferSize = 0;
            byte[] result = new byte[1024];
            while((bufferSize = input.read(result)) > 0){
                output.write(result, 0, bufferSize);
            }
            output.close();
            input.close();
            return output.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public static String getResponse(String url) throws IOException{
        return new String(getResponseByte(url));
    }

}
