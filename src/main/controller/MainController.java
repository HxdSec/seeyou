package main.controller;

import com.github.kevinsawicki.http.HttpRequest;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.common.VulnerableDetectTask;
import main.model.ItemModel;
import main.model.TaskModel;
import main.utils.FileUtil;

import javax.print.DocFlavor;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainController implements Initializable {

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

    private ObservableList<ItemModel> dataList = FXCollections.observableArrayList();

    @FXML
    private AnchorPane root;

    private Stage stage;



    @FXML
    private TextField txt_target;

    @FXML
    private TextArea area_result;

    @FXML
    private Button btn_check;



    @FXML
    private Button btn_check_list;

    @FXML
    private Button btn_import;

    @FXML
    private TextArea area_info;


    @FXML
    private TableView<ItemModel> table_list;


    @Override
    public void initialize(URL location, ResourceBundle resources) {



        //tableview init
        TableColumn urlCol = new TableColumn("URL");
        urlCol.prefWidthProperty().bind(table_list.widthProperty().multiply(0.8));

        urlCol.setCellValueFactory(new PropertyValueFactory<ItemModel, String>("url"));

        TableColumn statusCol = new TableColumn("状态");
        statusCol.prefWidthProperty().bind(table_list.widthProperty().multiply(0.15));
        statusCol.setCellValueFactory(new PropertyValueFactory<ItemModel, String>("status"));
        statusCol.setStyle("-fx-alignment:CENTER");

        table_list.getColumns().addAll(urlCol, statusCol);
        table_list.setItems(dataList);



    }




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

    @FXML
    void btn_import_click(ActionEvent event) {
        if (stage == null) {
            stage = (Stage) root.getScene().getWindow();
        }

        final FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        String filepath = file.getPath();
        List<String> list = FileUtil.Read(filepath);
        for (String url: list ) {
            dataList.add(new ItemModel(url,"未检测"));
        }

    }

    @FXML
    void btn_check_list_click(ActionEvent event) {

        dataList.stream().map(n->{
                return  new TaskModel(dataList.indexOf(n),n.getUrl(),n.getStatus(),"" );
                }).map(VulnerableDetectTask::new).peek(exec::execute).forEach(task-> {
                    task.setOnSucceeded(t -> {
                         TaskModel model = task.getValue();

                    });
                }

        );
    }


}
