import com.chipsk.im.client.config.AppConfiguration;
import com.chipsk.im.client.service.MsgService;
import com.chipsk.im.client.service.impl.MsgServiceImpl;
import com.chipsk.im.client.service.impl.RouteReqServiceImpl;
import com.chipsk.im.common.constant.Constants;
import com.chipsk.im.common.data.construct.TimerTask;
import com.chipsk.im.common.data.construct.ZSetTimer;
import com.chipsk.im.common.pojo.Task;
import com.chipsk.im.common.protocol.IMRequestProto;
import com.chipsk.im.common.proxy.ProxyManager;
import com.chipsk.im.route.api.vo.RouteApi;
import com.chipsk.im.route.api.vo.req.ChatReqVo;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("serial")
public class ZSetTimerTest {

    @Test
    public void testTimer() throws InterruptedException {
        ZSetTimer rt = new ZSetTimer();
        final SimpleDateFormat s = new SimpleDateFormat("hh:mm:ss:SSS");
        for (int i = 0; i < 2; i++) {
            System.out.println(s.format(new Date()) + "提交任务！");
            rt.addTask(new PrintTask(), 5, TimeUnit.SECONDS);
        }

        TimeUnit.SECONDS.sleep(7);
        System.out.println("你好");
        rt.stop();
    }
}

class PrintTask extends TimerTask implements Serializable{

    private static final long serialVersionUID = 1L;

    private String id;

    private OkHttpClient okHttpClient;

    @Override
    public void run() {

//        RouteApi routeApi = new ProxyManager<>(RouteApi.class, "http://127.0.0.1:7073/", okHttpClient).getInstance();
//        ChatReqVo chatReqVo = new ChatReqVo(1652189086370L, "hello");
//        Response response = null;
//        try {
//            response = (Response) routeApi.groupRoute(chatReqVo);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            response.body().close();
//        }

        AppConfiguration appConfiguration = new AppConfiguration();
        System.out.println(appConfiguration.getUserId());
        SimpleDateFormat s = new SimpleDateFormat("hh:mm:ss:SSS");
        System.out.println(s.format(new Date()) + "执行了！");
    }

    public PrintTask() {
        this(UUID.randomUUID().toString());
    }

    public PrintTask(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
