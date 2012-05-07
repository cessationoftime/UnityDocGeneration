package com.exiledstudios.unitydocs

import java.io.File

object Settings {
  // Internal Settings
  val DOC_SINCE = "3.4.0f5" // Change this to the newest Unity version.
  val DOC_EMPTY = "To be added.";
  val DOC_OVERWRITE = true
  val SHOW_WARNINGS = false

  // Our Paths
  val SOURCE_PATH = "Source/"
  val LOG_PATH = "Logs/"
  val RELEASE_PATH = "Release/"
  val RTF_PATH = "RTFs/"

  // System Paths
  val MONODOC_PATH = "c:\\Users\\cvanvranken\\gits\\mono\\scripts\\"
  val FRAMEWORKS_PATH = "c:\\Users\\cvanvranken\\gits\\MonoDevelop.Unity\\Documentation\\Libraries\\"
  val SCRIPTREFERENCE_PATH = "/Progra~2/Unity/Editor/Data/Documentation/Documentation/ScriptReference/"

  // MonoDocer Parsing Command
  //$monodoc_export_path = "export MONO_PATH=$MONOPATH:" . FRAMEWORKS_PATH . "/Mono.framework";
  val monodoc_command = Seq("bash", MONODOC_PATH + "monodocer -assembly:" + FRAMEWORKS_PATH + "UnityEditor.dll " + FRAMEWORKS_PATH + "UnityEditor.dll " + FRAMEWORKS_PATH + "UnityEngine-Debug.dll -path:" + SOURCE_PATH + " -pretty > " + LOG_PATH + "monodocer.log");
  //$monodoc_command = "bash " . MONODOC_PATH . "monodocer -assembly:" . FRAMEWORKS_PATH . "UnityEngine.dll " . FRAMEWORKS_PATH . "UnityEditor.dll " . FRAMEWORKS_PATH . "UnityEngine-Debug.dll -path:" . SOURCE_PATH . " -pretty > " . LOG_PATH . "monodocer.log";
  // MonoDoc Assembler Command				
  val mdassembler_command = Seq("bash", MONODOC_PATH + "mdassembler --ecma " + SOURCE_PATH +
    " --out " + RELEASE_PATH + "Unity > " + LOG_PATH +
    "mdassembler.log");

  // MonoDoc to VS
  // val mdocexport_command = "bash " + MONODOC_PATH + "monodocs2slashdoc " + SOURCE_PATH + " --out=" + RELEASE_PATH + "Unity.xml";
  val mdocexport_command = Seq("bash", MONODOC_PATH + "monodocs2slashdoc " + SOURCE_PATH + " --out=" + RELEASE_PATH + "Unity.xml");

  val monodoc_source_file = <?xml version="1.0"?>
                            <monodoc>
                              <node label="Unity" name="Unity" parent="libraries"/>
                              <source provider="ecma" basefile="Unity" path="Unity"/>
                            </monodoc>;

  val allowed_tags = Array("see");
  val hot_links = Map("character controller" -> "UnityEngine.CharacterController",
    "rigidbody" -> "UnityEngine.Rigidbody",
    "rigidbodies" -> "UnityEngine.Rigidbody",
    "MonoBehaviour" -> "UnityEngine.MonoBehaviour");
  val external_links = Array.empty[String];
  val remove_links = Array("GUI Scripting Guide",
    "Character animation examples",
    "Character Controller component");

  def dirExists(dir: String): Option[File] = {
    val f = new File(dir)
    if (f.exists) {
      Some(f)
    } else None
  }

  /**
   * create directory if not in existence
   */
  def mkdir(dir: String) =
    dirExists(dir) map { d =>
      d.mkdir
    }

  def checkExists(dir: String, errorMessage: String) =
    if (dirExists(dir).isEmpty) {
      print(errorMessage)
      System.exit(1)
    }

  def writeStringToFile(fileName: String, contents: String) = {
    val in = scala.io.Source.fromString(contents)
    val out = new java.io.PrintWriter(fileName)
    try { in.getLines().foreach(out.print(_)) }
    finally { out.close }

  }

  def moveDir(from: String, to: String) {
    new File(from).renameTo(new File(to))
  }

  /**
   * check if settings are valid and create directories
   */
  def apply() = {

    // Create Directories
    mkdir(SOURCE_PATH)
    mkdir(LOG_PATH)
    mkdir(RELEASE_PATH)
    mkdir(RELEASE_PATH + "MonoDevelop");
    mkdir(RELEASE_PATH + "VS");

    // Check System Locations
    checkExists(MONODOC_PATH, "MonoDoc Not Found: clone mono from github and set location of the scripts folder")
    checkExists(FRAMEWORKS_PATH, "Unity Framework Files Not Found")
    checkExists(SCRIPTREFERENCE_PATH, "Unity Script Reference Not Found")

    // Remove Old Logs & Misc
    new File(LOG_PATH + "monodocer.log").delete
    new File(LOG_PATH + "mdassembler.log").delete
    new File(LOG_PATH + "updateDocumentation.log").delete
    new File(RELEASE_PATH + "Unity.source").delete

    //write out the monodoc SourceFile
    writeStringToFile(RELEASE_PATH + "Unity.source", monodoc_source_file.toString)
  }

}