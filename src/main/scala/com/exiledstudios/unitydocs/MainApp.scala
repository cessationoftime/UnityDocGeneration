package com.exiledstudios.unitydocs

import java.io.File
import sys.process.stringSeqToProcess
import scala.io.Source

object ErrorLog {
  // Counters
  object warnings {
    var enumeration = 0
    var structure = 0
    var `class` = 0;
    def printOut = println("\nerrors: \nenumeration = " + enumeration + "\nstructure = " + structure + "\nclass = " + `class` + "\n\n")
  }

  object errors {
    var enumeration = 0
    var structure = 0
    var `class` = 0;

    def printOut = println("\nerrors: \nenumeration = " + enumeration + "\nstructure = " + structure + "\nclass = " + `class` + "\n\n")
  }

  def printOut = { warnings.printOut; errors.printOut }
}

object MainApp extends App {
  import Settings._
  // Link Map
  val unity = Map.empty[String, String];

  //initialize our settings, create folders etc
  Settings()

  // Execute Parse/Update of Actual Libraries
  //exec($monodoc_export_path);

  monodoc_command!
  //exec($monodoc_command_2);

  // Generate Content from Documentation
  ParseFirstPass.updateDocumentationSource;
  /*
  // Combine documentation
  mdassembler_command!

  // Export VS Compatible Docs
  mdocexport_command!

  // Move Release to Folders
  moveDir(RELEASE_PATH + "Unity.tree", RELEASE_PATH + "MonoDevelop/Unity.tree");
  moveDir(RELEASE_PATH + "Unity.source", RELEASE_PATH + "MonoDevelop/Unity.source");
  moveDir(RELEASE_PATH + "Unity.zip", RELEASE_PATH + "MonoDevelop/Unity.zip");
  moveDir(RELEASE_PATH + "Unity.xml", RELEASE_PATH + "VS/Unity.xml");

  ErrorLog.printOut
  */
}