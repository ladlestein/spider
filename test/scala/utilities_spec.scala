package com.nowanswers.spider

import org.specs2.mutable.Specification
import org.specs2.specification.Scope


class UtilitiesSpec extends Specification {
  "be able to find something" in new Scope {
    val xml = <stuff><guy size="medium"></guy><guy size="large"></guy></stuff>
    Utilities.findByAttribute(xml \\ "guy", "size", "large")
  }

}