# **Сервис авторизации**
## *Описание*
Реализуем сервис авторизации пользователей по логину и паролю. Ключевым в этом задании будет то, как ваше приложение будет реагировать на ошибки, которые наш сервис будет выбрасывать в разных случаях.

Для работы необходимо подготовить несколько классов:

* Создайте spring boot приложение и все классы контроллеры, сервисы и репозитории сделать бинами в вашем application context.

Запрос на разрешения будет приходить на контроллер:
```
@RestController
public class AuthorizationController {
    AuthorizationService service;
    
    @GetMapping("/authorize")
    public List<Authorities> getAuthorities(@RequestParam("user") String user, @RequestParam("password") String password) {
        return service.getAuthorities(user, password);
    }
}
```
Класс-сервис, который будет обрабатывать введенные логин и пароль, выглядит следующим образом.
```
public class AuthorizationService {
    UserRepository userRepository;

    List<Authorities> getAuthorities(String user, String password) {
        if (isEmpty(user) || isEmpty(password)) {
            throw new InvalidCredentials("User name or password is empty");
        }
        List<Authorities> userAuthorities = userRepository.getUserAuthorities(user, password);
        if (isEmpty(userAuthorities)) {
            throw new UnauthorizedUser("Unknown user " + user);
        }
        return userAuthorities;
    }

    private boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    private boolean isEmpty(List<?> str) {
        return str == null || str.isEmpty();
    }
}
```
Он принимает в себя логин и пароль и возвращает разрешения для этого пользователя, если такой пользователь найден и данные валидны. Если присланные данные неверны, тогда выкидывается InvalidCredentials:

```
public class InvalidCredentials extends RuntimeException {
    public InvalidCredentials(String msg) {
        super(msg);
    }
}
```
Если наш репозиторий не вернул никаких разрешений, либо вернул пустую коллекцию, тогда выкидывается ошибка UnauthorizedUser:
```
public class UnauthorizedUser extends RuntimeException {
    public UnauthorizedUser(String msg) {
        super(msg);
    }
}
```
Enum с разрешениями выглядит следующим образом:
```
public enum Authorities {
    READ, WRITE, DELETE
}
```
* Необходимо реализовать метод ```getUserAuthorities``` в классе ```UserRepository```, который возвращает либо разрешения, либо пустой массив.
```
public class UserRepository {
    public List<Authorities> getUserAuthorities(String user, String password) {
        return ...;
    }
}
```
Для проверки работоспособности можно из браузера сделать следующий запрос, заполнив <ИМЯ_ЮЗЕРА> и <ПАРОЛЬ_ЮЗЕРА> своими тестовыми данными:```localhost:8080/authorize?user=<ИМЯ_ЮЗЕРА>&password=<ПАРОЛЬ_ЮЗЕРА>```

* Теперь, когда весь код готов, необходимо написать обработчики ошибок, которые выкидывает сервис ```AuthorizationService```. Требования к ним такие:

1. На InvalidCredentials он должен обратно клиенту отсылать http статус с кодом **400** и телом в виде сообщения из exception'а
2. На UnauthorizedUser он должен обратно клиенту отсылать http статус с кодом **401** и телом в виде сообщения из exception'а и писать в консоль сообщение из exception'а

# **Валидация**
Теперь ваш контроллер должен принимать не два объекта отдельно, а один объект содержащий значения user и password. Соответственно и AuthorizationService теперь работает с одним объектом. При этом, API для клиента не изменилось и он отправляет запрос такого вида ```localhost:8080/authorize?user=<ИМЯ_ЮЗЕРА>&password=<ПАРОЛЬ_ЮЗЕРА>```. Также вы можете заметить, что вы также должны проверять объект на валидность с помощью аннотации @Valid. Подумайте, как вы должны валидировать поля объекта User:
```
@RestController
public class AuthorizationController {
    AuthorizationService service;
    
    @GetMapping("/authorize")
    public List<Authorities> getAuthorities(@Valid User user) {
        return service.getAuthorities(user);
    }
}
```
Сделать преобразование одного объекта в два вы можете с помощью своего HandlerMethodArgumentResolver и ,например, своей аннотации.

# **Прокси на nginx**
## *Описание*
Реализуем нашего первое приложение с обратным прокси перед ним. Напишем конфигурацию nginx, который будет возвращать статический сайт, с помощью которого можно обратиться к нашему сервису авторизации из прошлого домашнего задания.

1. Первым делом нужно создать html форму для авторизации, которую нам будет возвращать nginx. Этот файл нужно положить в соответствующую папку, откуда nginx сможет ее забрать.
```
<html>
    <body>
        <h1>Sign in form</h1>
    
        <form action="/authorize" method="get" target="_blank">
          <label for="user">User name:</label>
          <input type="text" id="user" name="user"><br><br>
          <label for="password">Password:</label>
          <input type="text" id="password" name="password"><br><br>
          <button type="submit">Submit</button>
        </form>
    </body>
</html>
```
2. Вам необходимо написать конфигурацию для nginx так, чтобы он при вызове http://localhost:8080/signin возвращал нам эту html страницу, а все остальное он проксировал на наше spring boot приложение, которое работает на вашем локальном порту. 

3. То, что вы напишите в конфигурации, добавьте в текстовый файл(формат файла любой, например, txt) в ваш проект с сервисом авторизации, запушьте в ваш репозиторий, и пришлите ссылку на репозиторий.

# **Задача Dockerfile**
Давайте соберем наш первый докер образ на основе нашего приложения авторизации, которое мы писали во втором домашнем задании(возьем чисто серверное нашего приложение без html из прошлого задания). Для этого мы сначала напишем наш Dockerfile, а затем, для удобства, напишем манифест для docker-compose

# *Описание*
1. Первым делом нам надо собрать jar архив с нашим spring boot приложением. Для этого в терминале в корне нашего проект выполните команду:

Для gradle: ```./gradlew clean build ```(если пишет Permission denied тогда сначала выполните chmod +x ./gradlew)

Для maven: ```./mvnw clean package ```(если пишет Permission denied тогда сначала выполните chmod +x ./mvnw)

2. Теперь можно начинать писать Dockerfile. Базовым образом возьмите openjdk:8-jdk-alpine и не забудьте открыть докеру порт(EXPOSE), на котором работает ваше приложение

3. Добавьте собранный jar в ваш образ(ADD). Если вы собирали с помощью maven, тогда jar будет лежать в папке target, а если gradle - в build/libs

4. Для удобства сборки образа и запуска контейнера нашего приложения, напишем docker-compose.yml. Контейнер назовите как вам больше нравится, а в его конфигурациях пропишите следующее:

* добавим build: ./ который скажет docker-compose что надо сначала собрать образ для этого контейнера
* добавим соответствие порта на хост машине и порта в контейнере для нашего приложения (аналог аргумента -p у команды docker run)
5. Два полученных файла добавьте в репозиторий вашего приложения и пришлите ссылка на него.

