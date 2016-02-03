package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Note(id: Pk[Long], title: String, body: String)

object Note {
  
  // -- Parsers
  
  /**
   * Parse a Project from a ResultSet
   */
  val simple = {
    get[Pk[Long]]("note.id") ~
    get[String]("note.title") ~
    get[String]("note.body") map {
      case id~title~body => Note(id, title, body)
    }
  }
  
  // -- Queries
    
  /**
   * Retrieve a Note by id.
   */
  def findById(id: Long): Option[Note] = {
    DB.withConnection { implicit connection =>
      SQL("select * from note where id = {id}").on(
        'id -> id
      ).as(Note.simple.singleOpt)
    }
  }

  /**
   * Retrieve all notes.
   */
  def findAll: Seq[Note] = {
    DB.withConnection { implicit connection =>
      SQL("select * from note").as(Note.simple *)
    }
  }  
  
  /**
   * Delete a note.
   */
  def delete(id: Long) {
    DB.withConnection { implicit connection => 
      SQL("delete from note where id = {id}").on(
        'id -> id
      ).executeUpdate()
    }
  }
  
     
  /**
   * Create a Note.
   */
  def create(note: Note): Note = {
     DB.withTransaction { implicit connection =>
       
       // Get the note id
       val id: Long = note.id.getOrElse {
         SQL("select nextval('note_seq')").as(scalar[Long].single)
       }
       
       // Insert the note
       SQL(
         """
           insert into note values (
             {id}, {title}, {body}
           )
         """
       ).on(
         'id -> id,
         'title -> note.title,
         'body -> note.body
       ).executeUpdate()
       
       note.copy(id = Id(id))
       
     }
  }
  def update(note: Note) = {
    DB.withConnection { implicit connection =>
      SQL("update note set title = {newTitle}, body = {newBody} where id = {id}").on(
        'id -> note.id.get, 'newTitle -> note.title, 'newBody -> note.body
      ).executeUpdate()
    }
  } 
}
