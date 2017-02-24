# QUILL-MAGICTABLE
Library that make using [Quill](http://getquill.io/) easier!

## Small overview
This library contains two main parts:
- MagicContext - annotation that allows you to change database easier
- Orm annotations - annotations that allow you to write less boilerplate code

## MagicContext

When you write code with [Quill](http://getquill.io/) you must choose
some database context like this:
```
val db = new PostgresAsyncContext[SnakeCase]("db")
```
This is suitable for many cases, but what if you need create application
that should work not only with postgres?
With this approach you will need to manually rewrite all code using this
db-context.
It might be easy if you switch from one jdbc driver to another.
But what if you want to switch postgres-jdbc to finagle-postgres?
This might be harder. But I have good news for you! MagicContext is what
you need
With this annotation you can create context like this:
```
@MagicContext
class MyContext
```
And then use it like this:
```
val db = new MyContext[SnakeCase]("db")
```
This annotation will read your *application.conf* file and use
specified context.

For example:

*application.conf*
```
magictable {
    database = "Postgres"
    future = "com.twitter.util.Future"
}
```
This will generate MyContext as context for postgres jdbc-driver with
wrapper of com.twiiter.util.Future.
To use wrapper of com.twitter.util.Future you must use function
*genericRun* instead of *run*

Example for upper *applcation.conf*:
```
@MagicContext
class MyContext

val db = new MyContext[SnakeCase]("db")

val someQuery = quote {
    query[TestTable].filter(_.id > 10)
}

db.run(someQuery) // this will return List[TestTable] (no magic used :))
db.genericRun(someQuery) // this will return com.twiiter.util.Future[List[TestTable]]
```

Also, if you don't want to manually create class for context,
you can just write this lines:
```
import tablemagic._
import tablemagic.db._
```

*db* is the context that use preferences from *application.conf* for key
"db" and use *SnakeCase* naming strategy

## ORM-Like features

Another part of library is orm-like features that allow you to make your code shorter.

Suppose you have two classes(and two tables):
```
case class User(id: Long, name: String)
case class UserToken(value: UUID, userId: Long)
```
If you want get user who owns some token you will need write following code:
```
def getUser(token: UserToken): User = {
    val userQuery = quote {
        query[User].filter(_.id == lift(token.userId))
    }
    genericRun(userQuery).head
}
```

Now let's see how we can do this with help of *@Table* and *@References*
annotations:
```
@Table
case class User(id: Long, name: String)

@Table
case class UserToken(value: UUID, @References[User] userId: Long)

def getUser(token: UserToken): User = token.user
```

As for me, seems a little easier:)

Sadly, at this moment, it's the only orm-like feature available, but
I plan to add more features later.

### Small guide to solve problems when using of *@References*

- What if you need to return more than one object?

To be able to write method like this
```
def getUsers(token: UserToken): List[User] = token.user
```
You need change *ConnectionType* in *@Refernces* declaration like this:
```
@Table
case class UserToken
    (
    value: UUID,
    @References[User](ConnectionType.Many) userId: Long
    )
```
- What if you need to return nullable object?

To able to write method like this
```
def getUser(token: UserToken): Option[User] = token.user
```
You need change *ConnectionType* like this:
```
@Table
case class UserToken
    (
    value: UUID,
    @References[User](ConnectionType.ZeroOrOne) userId: Long
    )
```
- What if need to use different naming

Default algorithm works like this:
For field `userId` from examples indicated above:
  - generated field will have name: *user*
  - field in other class assumed to have this name: *id*
To change this you must write following:
```
@References(ConnectionType, generatedFieldName, tableFieldName)
```
You must indicate *ConnectionType* and you can't use named arguments.
- I need to use *@Table* with not standard db context(*tablemagic.db*)
Simple write path to your context

For example:
```
@Table("example.package.path.SomeObject.contextName")
case class Test(id: Long)
```
## Extension package

Also, there is extension package that contains context for Oracle database.
You can use it, but this context is not tested yet.