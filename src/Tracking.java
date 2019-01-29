import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.*;

public class Tracking {

    public static void main(String[] args) throws Exception {
        long queryCode1 = 7260828444L;

            String dhlno = post(queryCode1);
            if (dhlno == "暂无DHL单号！") {
                System.out.println("运单号：" + (queryCode1) + " " + dhlno);
            } else {
                String bqcstatus = kuaidi100(queryCode1, "bqcwl");
                String dhlstatus = kuaidi100(Long.parseLong(dhlno, 10), "dhlen");
                System.out.println("运单号：" + (queryCode1) + "，DHL单号：" + dhlno
                        + "\nBQC状态：" + bqcstatus
                        + "\nDHL状态：" + dhlstatus);
        }
    }

    public static String kuaidi100(long queryCode, String type) throws Exception {
        String host = "https://www.kuaidi100.com";
        String path = "/query";
        String urlSend = host + path + "?type=" + type + "&postid=" + queryCode;
        URL url = new URL(urlSend);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        String json = read(httpURLConnection.getInputStream());
            JSONObject dataJson = new JSONObject(json);
            JSONArray data = dataJson.getJSONArray("data");
            JSONObject[] info = new JSONObject[data.length()];
            for (int i = 0; i < info.length; i++) {
                info[i] = data.getJSONObject(i);
            }

            String[] station = new String[info.length];
            for (int i = 0; i < station.length; i++) {
                station[i] = info[i].getString("context");
            }

            String[] time = new String[info.length];
            for (int i = 0; i < time.length; i++) {
                time[i] = info[i].getString("time");
            }
        return time[0] + " " + station[0];
    }

    public static String post(long queryCode) throws Exception {
        String host = "http://www.1001000.com";
        String path = "/newwl/page/queryTrack";
        String urlSend = host + path + "?queryCode=" + queryCode;
        URL url = new URL(urlSend);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        String html = read(httpURLConnection.getInputStream());
        String result = "";
        int count = 0;
        char[] arr = html.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            if ('0' <= arr[i] && '9' >= arr[i]) {  //当前的是数字
                count = 1;                         //初始化计算器
                int index = i;                     //在后面的循环存储截至索引
                for (int j = i + 1; j < arr.length; j++) {
                    if ('0' <= arr[j] && '9' >= arr[j]) {
                        count++;
                        index = j;
                    } else {
                        break;
                    }
                } if (count == 10) {
                    result = html.substring(i, index + 1);
                }
            } else {
                continue;
            }
        }
        if (result.equals("")) {
            result = "暂无DHL单号！";
        }
        return result;
    }

    private static String read(InputStream is) throws IOException {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        while ((line = br.readLine()) != null) {
            line = new String(line.getBytes(), "utf-8");
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }
}
