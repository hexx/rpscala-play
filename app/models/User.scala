package models

import play.api.db.DB
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class User(
  id: Pk[Int],
  login: String,
  name: String,
  email: Option[String],
  sex: Int
)

object User {
  val simple = {
    get[Pk[Int]]("id") ~
    get[String]("login") ~
    get[String]("name") ~
    get[Option[String]]("email") ~
    get[Int]("sex") map {
      case id~login~name~email~sex =>
        User(id, login, name, email, sex)
    }
  }

  def insert(user: User) = {
    DB.withConnection { implicit c =>
      SQL("""
          insert into User (login, name, email, sex)
          values ({login}, {name}, {email}, {sex})
          """
      ).on(
        "login" -> user.login,
        "name"  -> user.name,
        "email" -> user.email,
        "sex"   -> user.sex
      ).executeUpdate()
    }
  }

  def selectAll() = {
    DB.withConnection { implicit c =>
      SQL("SELECT * FROM User").as(User.simple *)
    }
  }

  def select(id: Int) = {
    DB.withConnection { implicit c =>
      SQL("SELECT * FROM User where id = {id}").on("id" -> id).as(User.simple.singleOpt)
    }
  }

  def update(user: User) = {
    DB.withConnection { implicit c =>
      SQL("""
          update User set
          login={login}, name={name}, email={email}, sex={sex}
          where id={id}
          """
      ).on(
        "login" -> user.login,
        "name"  -> user.name,
        "email" -> user.email,
        "sex"   -> user.sex,
        "id"    -> user.id
      ).executeUpdate()
    }
  }

  def delete(id: Int) = {
    DB.withConnection { implicit c =>
      SQL("delete User where id = {id}").on("id" -> id).execute()
    }
  }
}
