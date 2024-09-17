import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider

class MyCustomPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Register a custom task
        val customTask: TaskProvider<CustomTask> = project.tasks.register("customTask", CustomTask::class.java) {
            group = "Custom"
            description = "Executes the custom task."
        }

        // You can add more plugin logic here
    }
}

// Define a custom task if needed
abstract class CustomTask : org.gradle.api.DefaultTask() {
    @org.gradle.api.tasks.TaskAction
    fun perform() {
        println("Hello from Custom Task!")
    }
}
