package com.sazke.com.spring;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

@Route
@PWA(name = "Project Base for Vaadin Flow with Spring", shortName = "Project Base")
public class MainView extends VerticalLayout {

    public MainView(@Autowired MessageBean bean) {

        TextField textField = new TextField();
        textField.setLabel("Ingresa nombre a generar");
        textField.setPlaceholder("Nombre");
        Button button = new Button("Click me",
                e -> {
                    try {
                        generateController(textField.getValue());
                        generateRepository(textField.getValue());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
        add(textField);
        add(button);
    }

    private void generateController(String name) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("/home/sazke/Desktop/" + name + "Controller.java", true));
        generateAnnotations(writer, name);
        generateFindAll(writer, name);
        generateFindById(writer, name);
        generateCreate(writer,name);
        generatePut(writer,name);
        writer.append("}");
        writer.close();
    }

    private void generateRepository(String name) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("/home/sazke/Desktop/" + name + "Repository.java", true));
       generateImportsRepository(writer,name);
        writer.close();
    }


    private BufferedWriter generateImportsRepository(BufferedWriter writer, String name) throws IOException {
        writer.append("import org.springframework.data.jpa.repository.JpaRepository\n\n");
        writer.append("interface "+name+"Repository : JpaRepository<"+name+"Models,Long>");
        return writer;
    }

    private BufferedWriter generateAnnotations(BufferedWriter writer, String name) throws IOException {
        writer.append("@CrossOrigin\n");
        writer.append("@RestController\n");
        writer.append("@RequestMapping(\"/v1/" + name.toLowerCase() + "\")\n");
        writer.append("class "+name+"Controller {\n\n");
        writer.append("@Autowired\n");
        writer.append("lateinit var " + name.toLowerCase() + "Repository: " + name + "Repository\n");
        return writer;
    }

    private BufferedWriter generateFindAll(BufferedWriter writer, String name) throws IOException {
        writer.append("@GetMapping\n");
        writer.append("fun findAll(): MutableList<" + name + "Models> = " + name.toLowerCase() + "Repository.findAll()\n\n");
        return writer;
    }

    private BufferedWriter generateFindById(BufferedWriter writer, String name) throws IOException {
        writer.append("@GetMapping(\"{id}\")\n");
        writer.append("fun findById(@PathVariable(\"id\") id: Long): ResponseEntity<" + name + "Models> = " + name.toLowerCase() + "Repository.findById(id).map { s -> ResponseEntity.ok(s) }\n" +
                ".orElse(ResponseEntity.notFound().build())\n\n");
        return writer;
    }

    private BufferedWriter generateCreate(BufferedWriter writer, String name) throws IOException {
        writer.append("@PostMapping\n");
        writer.append("fun create(@Valid @RequestBody "+name.toLowerCase()+"Model: "+name+"Models): "+name+"Models = "+name.toLowerCase()+"Repository.save("+name.toLowerCase()+"Model)\n\n");
        return writer;
    }

    private BufferedWriter generatePut(BufferedWriter writer, String name) throws IOException {
        writer.append("@PutMapping(\"{id}\")\n");
        writer.append("fun update(@PathVariable(\"id\") id: Long, @Valid @RequestBody "+name.toLowerCase()+"Model: " + name + "Models): " + name + "Models {\n\n");
        return writer;
    }

}
