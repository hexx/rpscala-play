package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import anorm._

import models._
import views._

object Application extends Controller {
  val userForm = Form(
    mapping(
      "id"    -> number,
      "login" -> nonEmptyText,
      "name"  -> nonEmptyText,
      "email" -> optional(email),
      "sex"   -> number
    )((id: Int, login: String, name: String, email: Option[String], sex: Int) => User(Id(id), login, name, email, sex))
     ((user: User) => Some(user.id.get, user.login, user.name, user.email, user.sex))
  )

  def index = Action {
    Ok(views.html.user.UserCreate(userForm))
  }

  def create = Action { implicit request =>
    userForm.bindFromRequest.fold(
      errors => BadRequest(views.html.user.UserCreate(errors)),
      form => {
        User.insert(form)
        Redirect(routes.Application.list)
      }
    )
  }

  def list = Action {
    Ok(views.html.user.UserList(User.selectAll))
  }

  def modifyById(id: Int) = Action {
    val user = User.select(id).get
    Ok(views.html.user.UserModify(userForm.fill(user)))
  }

  def modify() = Action { implicit request =>
    userForm.bindFromRequest.fold(
      errors => BadRequest(views.html.user.UserModify(errors)),
      form => {
        User.update(form)
        Redirect(routes.Application.list)
      }
    )
  }

  def delete(id: Int) = Action {
    User.delete(id)
    Redirect(routes.Application.list)
  }
}
