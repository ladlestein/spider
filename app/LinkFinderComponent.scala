package spider

import xml.Document

/**
 * Created with IntelliJ IDEA.
 * User: ladlestein
 * Date: 7/9/12
 * Time: 6:31 PM
 * To change this template use File | Settings | File Templates.
 */

trait LinkFinderComponent {

  val linkfinder: LinkFinder

  trait LinkFinder {

    def links(page: Document)

  }

}
