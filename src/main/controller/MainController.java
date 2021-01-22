package main.controller;

import com.github.kevinsawicki.http.HttpRequest;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.paint.Color;
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



    private ObservableList<TaskModel> dataList = FXCollections.observableArrayList();

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
    private TableView<TaskModel> table_list;


    @Override
    public void initialize(URL location, ResourceBundle resources) {



        //tableview init
        TableColumn urlCol = new TableColumn("URL");
        urlCol.prefWidthProperty().bind(table_list.widthProperty().multiply(0.8));

        urlCol.setCellValueFactory(new PropertyValueFactory<TaskModel, String>("url"));

        TableColumn statusCol = new TableColumn("状态");
        statusCol.prefWidthProperty().bind(table_list.widthProperty().multiply(0.15));
        statusCol.setCellValueFactory(new PropertyValueFactory<TaskModel, String>("status"));
        statusCol.setStyle("-fx-alignment:CENTER");
        statusCol.setCellFactory(column -> new TableCell<TaskModel, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item);
                    if (item.contains("存在漏洞")){
                        this.setTextFill(Color.RED);
                    }else{
                        this.setTextFill(Color.BLACK);
                    }
                }
            }
        });
        table_list.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<TaskModel>() {
                    @Override
                    public void changed(
                            ObservableValue<? extends TaskModel> observableValue,
                            TaskModel oldItem, TaskModel newItem) {
                            area_info.setText(newItem.getResult());
                    }
                });

        table_list.getColumns().addAll(urlCol, statusCol);
        table_list.setItems(dataList);



    }




    @FXML
    void btn_check_click(ActionEvent event) {

    }

    @FXML
    void btn_import_click(ActionEvent event) {
        if (stage == null) {
            stage = (Stage) root.getScene().getWindow();
        }

        final FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null){
            String filepath = file.getPath();
            List<String> list = FileUtil.Read(filepath);
            for (String url: list ) {
                url = url + "seeyon/thirdpartyController.do.css/..;/ajax.do";
                dataList.add(new TaskModel(list.indexOf(url),url,"未检测",""));
            }
        }
    }

    @FXML
    void btn_check_list_click(ActionEvent event) {

        dataList.stream().map(n->{
                return  new TaskModel(dataList.indexOf(n),n.getUrl(),n.getStatus(),"" );
                }).map(VulnerableDetectTask::new).peek(exec::execute).forEach(task-> {
                    task.setOnSucceeded(t -> {
                         TaskModel model = task.getValue();
                         dataList.set(model.getId(),model);


                    });
                    task.progressProperty().addListener(observable -> {

                    });
                }

        );
    }


}
