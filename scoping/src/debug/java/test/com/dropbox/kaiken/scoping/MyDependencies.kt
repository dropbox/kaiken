package test.com.dropbox.kaiken.scoping

interface MyDependencies {
    fun helloWorldGreeter(): HelloWorldGreeter
}

interface HelloWorldGreeter {
    fun sayHello(): String
}
