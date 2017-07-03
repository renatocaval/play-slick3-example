package controllers

import javax.inject.Inject

import models.{ProjectRepo, TaskRepo}
import play.api.mvc.{Action, BaseController, Controller, ControllerComponents}
import play.api.mvc.{ AbstractController, AnyContent }
import com.github.takezoe.slick.blocking.BlockingH2Driver.blockingApi._
import com.mohiva.play.silhouette
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions._
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import utils.AuthEnv

import scala.concurrent.{ExecutionContext, Future}

class Application @Inject()(
                             projectRepo: ProjectRepo,
                             taskRepo: TaskRepo,
                             silhouette: Silhouette[AuthEnv],
                             val controllerComponents: ControllerComponents
                           )(protected val dbConfigProvider: DatabaseConfigProvider,
                             val ex: ExecutionContext )
                           extends BaseController {

  val db = dbConfigProvider.get[JdbcProfile].db


  def test = silhouette.SecuredAction { implicit request: SecuredRequest[AuthEnv, AnyContent] =>
    Ok("oll korrekt")
  }


  def addTaskToProject(color: String, projectId: Long) = silhouette.SecuredAction { implicit request: SecuredRequest[AuthEnv, AnyContent] =>
    db.withSession { implicit session =>
      projectRepo.addTask(color, projectId)
      Redirect(routes.Application.projects(projectId))
    }
  }

  def modifyTask(taskId: Long, color: Option[String]) = silhouette.SecuredAction { implicit request: SecuredRequest[AuthEnv, AnyContent] =>
      db.withSession { implicit session =>

        val updatedRows = taskRepo.partialUpdate(taskId, color, None, None)

        Ok(s"Rows affected : $updatedRows")
      }
  }

  def createProject(name: String)= silhouette.SecuredAction { implicit request: SecuredRequest[AuthEnv, AnyContent] =>
    db.withSession { implicit session =>
      val id = projectRepo.create(name)
      Ok(s"project $id created")
    }
  }

  def listProjects = silhouette.SecuredAction { implicit request: SecuredRequest[AuthEnv, AnyContent] =>
    db.withSession { implicit session =>
      val projects = projectRepo.all
       Ok(views.html.projects(projects))
    }
  }

  def projects(id: Long) = silhouette.SecuredAction { implicit request: SecuredRequest[AuthEnv, AnyContent] =>
    db.withSession { implicit session =>
      val project =  projectRepo.findById(id).get
      val tasks = taskRepo.findByProjectId(id)
      Ok(views.html.project(project, tasks))
    }
  }

  def delete(name: String) = silhouette.SecuredAction { implicit request: SecuredRequest[AuthEnv, AnyContent] =>
    db.withSession { implicit session =>
      projectRepo.delete(name)
      Ok(s"project $name deleted")
    }
  }

}
