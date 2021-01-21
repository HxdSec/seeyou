package main.controller;

import com.github.kevinsawicki.http.HttpRequest;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainController {

    private static final Random RNG = new Random();
    private static final int MAX_THREADS = 4 ;

    private final Executor exec = Executors.newFixedThreadPool(MAX_THREADS, runnable -> {
        Thread t = new Thread(runnable);
        t.setDaemon(true);
        return t ;
    });

    public static class FileProcessingTask extends Task<Integer> {

        private  Integer id = 0 ;

        public FileProcessingTask(Integer id) {
            this.id = id ;
        }

        @Override
        public Integer call() throws Exception {


            // dummy processing, in real life read file and do something with it:
            int delay = RNG.nextInt(50) + 50 ;
            for (int i = 0 ; i < 100; i++) {
                Thread.sleep(delay);
                updateProgress(i, 100);

                // check for cancellation and bail if cancelled:
                if (isCancelled()) {
                    updateProgress(0, 100);
                    break ;
                }
            }

            return delay ;
        }
    }




    @FXML
    private TextField txt_target;

    @FXML
    private TextArea area_result;

    @FXML
    private Button btn_check;

    @FXML
    void btn_check_click(ActionEvent event) {

        //String response = HttpRequest.get("http://www.baidu.com").body();
        ArrayList<Integer> files = new ArrayList<>();
        for(int i=0;i<10;i++){
            files.add(i);
        }

        files.stream().map(FileProcessingTask::new).peek(exec::execute).forEach(task-> {

            task.progressProperty().addListener(observable -> {
                area_result.appendText(task.id.toString() + ":" + task.getProgress() + "\n");
            });


            task.setOnSucceeded(wse -> {
                area_result.appendText(task.id.toString() + ":" + task.getValue() + "\n");
            });
                }

         );

    }


}
