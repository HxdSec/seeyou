package main.common;

import com.github.kevinsawicki.http.HttpRequest;
import javafx.concurrent.Task;
import main.model.TaskModel;

public  class VulnerableDetectTask extends Task<TaskModel> {

    private TaskModel model;

    public VulnerableDetectTask(TaskModel model) {
        this.model = model ;
    }

    @Override
    public TaskModel call() throws Exception {

        try {
            HttpRequest request =  HttpRequest.get(model.getUrl());
            if (request.ok()) {
               String result = request.body();
               model.setResult(result);
               if (result.contains("java.lang.NullPointerException:null")){
                   model.setStatus("存在漏洞");
                }else{
                   model.setStatus("检测结束");
               }
            }
            return model;
        } catch (HttpRequest.HttpRequestException exception) {
            model.setStatus("发生错误");
            return model;
        }
    }
}
