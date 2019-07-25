package com.sazke.com.spring;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

@Route
@PWA(name = "Project Base for Vaadin Flow with Spring", shortName = "Project Base")
public class MainView extends VerticalLayout {

    public MainView(@Autowired MessageBean bean) {
        //Notification
        Notification notification = new Notification("Controllers and Repository created", 3000, Notification.Position.TOP_CENTER);
        //Progess
        ProgressBar progressBar = new ProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        progressBar.setValue(0.5);
        //Input
        TextField textField = new TextField();
        textField.setLabel("Nombre a generar");
        textField.setPlaceholder("Nombre");
        //Package
        TextField pack = new TextField();
        pack.setLabel("Nombre del package");
        pack.setPlaceholder("Nombre");
        Button button = new Button("Click me",
                e -> {
                    try {

                        progressBar.setVisible(true);
                        generateController(textField.getValue());
                        generateRepository(textField.getValue());
                        notification.open();
                        progressBar.setVisible(false);

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
        add(progressBar);
        add(pack);
        add(textField);
        add(button);

    }

    private void generateController(String name) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("/home/sazke/Desktop/" + name + "Controller.kt", true));
        generateImportsController(writer,name);
        generateAnnotations(writer, name);
        generateFindAll(writer, name);
        generateFindById(writer, name);
        generateCreate(writer, name);
        generatePut(writer, name);
        writer.append("}");
        writer.close();
    }

    private BufferedWriter generateImportsController(BufferedWriter writer, String name) throws IOException {
        writer.append("import org.springframework.beans.factory.annotation.Autowired\n");
        writer.append("import org.springframework.dao.DataAccessException\n");
        writer.append("import org.springframework.http.HttpStatus\n");
        writer.append("import org.springframework.http.ResponseEntity\n");
        writer.append("import org.springframework.web.bind.annotation.*\n");
        writer.append("import java.util.*\n");
        writer.append("import javax.validation.Valid\n");
        writer.append("import kotlin.collections.HashMap\n\n");
        return writer;








    }

    private void generateRepository(String name) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("/home/sazke/Desktop/" + name + "Repository.kt", true));
        generateImportsRepository(writer, name);
        writer.close();
    }


    private BufferedWriter generateImportsRepository(BufferedWriter writer, String name) throws IOException {

        writer.append("interface " + name + "Repository: JpaRepository<" + name + "Models,Long>");
        return writer;
    }

    private BufferedWriter generateAnnotations(BufferedWriter writer, String name) throws IOException {
        writer.append("@CrossOrigin\n");
        writer.append("@RestController\n");
        writer.append("@RequestMapping(\"/v1/" + name.toLowerCase() + "\")\n");
        writer.append("class " + name + "Controller {\n\n");
        writer.append("@Autowired\n");
        writer.append("lateinit var " + name.toLowerCase() + "Repository: " + name + "Repository\n\n");
        return writer;
    }

    private BufferedWriter generateFindAll(BufferedWriter writer, String name) throws IOException {
        writer.append("@GetMapping\n");
        writer.append("fun findAll(): MutableList<" + name + "Models> = " + name.toLowerCase() + "Repository.findAll()\n\n");
        return writer;
    }

    private BufferedWriter generateFindById(BufferedWriter writer, String name) throws IOException {
        writer.append("@GetMapping(\"{id}\")\n");
        writer.append(" fun findById(@PathVariable(\"id\") id: Long): ResponseEntity<Any> { \n");
        writer.append(" val response = HashMap<String, Any>() \n");
        writer.append("val e: Optional<" + name + "Model>\n");
        writer.append("try { \n");
        writer.append("e = " + name.toLowerCase() + "Repository.findById(id) \n");
        writer.append("   } catch (e: DataAccessException) {\n");
        writer.append(" response[\"message\"] = \"Something go wrong consulting database\"\n");
        writer.append("response[\"error\"] = \"${e.message} : ${e.mostSpecificCause.message}\"\n");
        writer.append("println(\"Error ${e.mostSpecificCause.message()}\")\n");
        writer.append(" return ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR)\n  }\n");
        writer.append("     if (!e.isPresent) {\n");
        writer.append("response[\"message\"] = \"The id: ${id} not exist!\n");
        writer.append("return ResponseEntity(response, HttpStatus.NOT_FOUND)\n}\n");
        writer.append("return ResponseEntity(e.get(), HttpStatus.OK)\n\n");
        return writer;
    }

    private BufferedWriter generateCreate(BufferedWriter writer, String name) throws IOException {
        writer.append("@PostMapping\n");
        writer.append("fun create(@Valid @RequestBody " + name.toLowerCase() + "Models: " + name + "Model): ResponseEntity<Any>{\n");
        writer.append("val response = HashMap<String, Any>()\n");
        writer.append("val e:" + name + "Model\n");
        writer.append("try {\n e = " + name + "Repository.save(" + name.toLowerCase() + "Models)\n}");
        writer.append("catch (e: DataAccessException) {\n");
        writer.append("println(\"Error ${e.mostSpecificCause.message()}\")\n");
        writer.append("response[\"message\"] = \"Something go wrong inserting at database in" + name + "\"\n");
        writer.append("response[\"error\"] = \"${e.message} : ${e.mostSpecificCause.message}\"\n");
        writer.append(" return ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR)\n}\n");
        writer.append("response[\"message\"] = \"The " + name + " has been inseted\"\n response[\"entity\"] = e");
        writer.append("return  ResponseEntity(response, HttpStatus.CREATED)\n\n");
        return writer;
    }

    private BufferedWriter generatePut(BufferedWriter writer, String name) throws IOException {
        writer.append("@PutMapping(\"{id}\")\n");
        writer.append("fun update(@PathVariable(\"id\") id: Long, @Valid @RequestBody " + name.toLowerCase() + "Model: " + name + "Models): " + name + "Models {\n\n");
        return writer;
    }

}
