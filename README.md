Arquillian SwitchYard Extension
====

Mock SwitchYard services using MockHandler helper or a custom ExchangeHandler.

* Build

```mvn clean install```

* Maven

```xml
<dependency>
    <groupId>org.jboss.arquillian.extension</groupId>
    <artifactId>arquillian-switchyard</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
```

* Usage

```java
@RunWith(Arquillian.class)
public class BeanAServiceIT {

    @Inject
    private BeanAService beanAService;
    @Inject
    private SwitchYardMockRegister mockRegister;
    
    private final QName beanBReference = new QName("urn:com.company:app:1.0", "BeanBService");

    @Deployment
    public static Archive<?> createDeployment() {
        ...
    }

    @Test
    public void test() {
        final MockHandler mock = new MockHandler();
        mockRegister.registerInOutService(beanBReference, mock);
        final BeanB beanB = new BeanB();
        beanB.setId(1L);
        mock.replyWithOut(beanB);
        
        beanAService.doSomething();
    }

```
