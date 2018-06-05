import com.sun.net.httpserver.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.Executors;

//jdk自带轻量级http server例子  http://127.0.0.1:8080/server
public class HttpServerDemo {
    public static void main(String[] args) throws IOException {
        InetSocketAddress addr = new InetSocketAddress(8080);
        HttpServer server = HttpServer.create(addr, 0);

        server.createContext("/server", new MyHandler());
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println("Server is listening on port 8080");
    }
}

class MyHandler implements HttpHandler {
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        if (requestMethod.equalsIgnoreCase("GET")) {
            Headers responseHeaders = exchange.getResponseHeaders();
            responseHeaders.set("Content-Type", "text/plain");
            // responseHeaders.set("Content-Type", "text/html; charset=utf-8");
            exchange.sendResponseHeaders(200, 0);

            OutputStream responseBody = exchange.getResponseBody();





            Headers requestHeaders = exchange.getRequestHeaders();
            Set<String> keySet = requestHeaders.keySet();
            Iterator<String> iter = keySet.iterator();
            while (iter.hasNext()) {
                String key = iter.next();
                List values = requestHeaders.get(key);
                String s = key + " = " + values.toString() + "\n";
                responseBody.write(s.getBytes());
            }

            Map <String,String> cxt = MyHandler.queryToMap(exchange.getRequestURI().getQuery());
//            HttpContext cxt = exchange.getHttpContext();
            Set<String> keySetParm =  cxt.keySet();
            Iterator<String> iterParm = keySetParm.iterator();
            while (iterParm.hasNext()) {
                String keyParm = iterParm.next();
                String value = cxt.get(keyParm);
                String s = keyParm + " = " + value + "\n";
                responseBody.write(s.getBytes());
            }


            responseBody.write("jdk自带轻量级http server例子".getBytes());
            responseBody.close();
        }
    }

    /**
     * returns the url parameters in a map
     * @param query
     * @return map
     */
    public static Map<String, String> queryToMap(String query){
        Map<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length>1) {
                result.put(pair[0], pair[1]);
            }else{
                result.put(pair[0], "");
            }
        }
        return result;
    }

}
