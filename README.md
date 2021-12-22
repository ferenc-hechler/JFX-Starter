# JFX-Starter

Experiments with JFX

# Further Infos:

* https://yumberc.github.io/FileBrowser/FileBrowser.html
** https://github.com/yumberc/yumberc.github.io/blob/master/FileBrowser/FileBrowser.zip
* http://tutorials.jenkov.com/javafx/treetableview.html
* https://gist.github.com/jewelsea/5174074


## setup junit 5 in pom 

https://howtodoinjava.com/junit5/junit5-maven-dependency/

## hello world in JFX

https://openjfx.io/openjfx-docs/

### add jfx installation to path

```
set PATH_TO_FX="C:\DEV\SDKs\javafx-sdk-17.0.1\lib"
```

### sample hello-world 

https://github.com/openjfx/samples/blob/master/HelloFX/CLI/hellofx/HelloFX.java

```
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class HelloFX extends Application {

    @Override
    public void start(Stage stage) {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label l = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        Scene scene = new Scene(new StackPane(l), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
```

### compile on CLI

```
javac --module-path %PATH_TO_FX% --add-modules javafx.controls HelloFX.java
```


