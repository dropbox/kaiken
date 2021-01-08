package test.com.dropbox.kaiken.testing

interface MyDependencies {
    fun helloWorldGreeter(): HelloWorldGreeter
}

interface HelloWorldGreeter {
    fun sayHello(): String
}
