package com.nowanswers.spider
import xml.{Node, NodeSeq}

/**
 * Created with IntelliJ IDEA.
 * User: ladlestein
 * Date: 10/11/12
 * Time: 11:06 PM
 * To change this template use File | Settings | File Templates.
 */
object Utilities {

  def findByAttribute (seq : NodeSeq, name : String, value : String) : Option[ Node ] = {
    seq.find {
      node => {
        val attribute = node.attribute (name)
        val attributeValue = attribute map {
          x => x head
        } map {
          x => x.text
        }
        attributeValue == Some (value)
      }
    }
  }

}
