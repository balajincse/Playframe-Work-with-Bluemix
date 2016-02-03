package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import play.api.libs.json._
import play.api.libs.functional.syntax._

import java.util.{Date}

import anorm._

import models._
import views._


/**
 * Manage notes related operations.
 */
object Notes extends Controller {

	implicit val rds = (
		(__ \ 'title).read[String] and
		(__ \ 'note).read[String]
	) tupled

  def create = Action(parse.json) { request =>
    request.body.validate[(String, String)].map { 
      case (title, note) => {
      	val n = Note.create(Note(NotAssigned, title, note))
      	Ok(Json.obj("id" -> n.id.get, "title" -> n.title, "note" -> n.body))
      }
    }.recoverTotal{
      e => BadRequest("Detected error:"+ JsError.toFlatJson(e))
    }
  }

  def retrieve(sid: String) = Action {
  	val nid = sid.toLong
  	Note.findById(nid) map {
  		case note: Note => Ok(Json.obj("id" -> note.id.get, "title" -> note.title, "note" -> note.body))
  	} getOrElse(BadRequest("Invalid ID"))
  }  

  def retrieveAll = Action {
  	val notes = 
  		for (note <- Note.findAll) 
			yield Json.obj(
				"id" -> note.id.get,
				"title" -> note.title,
				"note" -> note.body)
	   Ok(Json.arr(notes))
  }

  def delete(sid: String) = Action {
  	Note.findById(sid.toLong) map {
  		case note: Note => {
  			Note.delete(note.id.get)
  			Ok(Json.obj("id" -> note.id.get, "title" -> note.title, "note" -> note.body))
  		}
  	} getOrElse(BadRequest("Invalid ID"))	
  }

  def update(sid: String) = Action(parse.json) { request =>
    request.body.validate[(String, String)].map { 
      case (title, note) => {
      	Note.findById(sid.toLong) map {
      		case n: Note => {
      			Note.update(Note(n.id, title, note))
      			Ok(Json.obj("id" -> n.id.get, "title" -> title, "note" -> note))
      		}
      	} getOrElse(BadRequest("Invalid ID"))
      }
    }.recoverTotal{
      e => BadRequest("Detected error:"+ JsError.toFlatJson(e))
    }  
  }  
}