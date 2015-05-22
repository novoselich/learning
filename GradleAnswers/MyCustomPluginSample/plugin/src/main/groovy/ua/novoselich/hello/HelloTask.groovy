package ua.novoselich.hello

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

class HelloTask extends DefaultTask {

    @Input
    HelloExtension values

    private String trueName = "Walter White"
    private String trueMessage = "God damn right!"
    private String falseMessage = "You will die!"

    @TaskAction
    void sayHello() {
        println "${values.myName}!"

        println( trueName.equals(values.myName)
                ? trueMessage
                : falseMessage)
    }
}