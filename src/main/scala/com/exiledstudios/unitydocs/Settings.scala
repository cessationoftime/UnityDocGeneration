package com.exiledstudios.unitydocs

import java.io.File

object Settings {
  // Internal Settings
  val DOC_SINCE = "3.4.0f5" // Change this to the newest Unity version.
  val DOC_EMPTY = "To be added.";
  val DOC_OVERWRITE = true
  val SHOW_WARNINGS = false

  // Our Paths
  val DOCUMENTATION_PATH = "Documentation/"
  val SOURCE_PATH = DOCUMENTATION_PATH + "Source/"
  val LOG_PATH = DOCUMENTATION_PATH + "Logs/"
  val RELEASE_PATH = DOCUMENTATION_PATH + "Release/"
  val RTF_PATH = DOCUMENTATION_PATH + "RTFs/"
  import sys.process.stringSeqToProcess

  val bash = """C:\cygwin\bin\bash"""
  def cygpath(path: String) = {
    val cyg = """C:\cygwin\bin\cygpath"""
    (Seq(cyg, path)!!).trim
  }

  // System Paths
  def mdoc(mode: String) = "c:/progra~2/Unity/Editor/Data/Mono/lib/mono/2.0/mdoc " + mode + " "
  //val MONODOC_PATH = "~/gits/mono/scripts/"
  val FRAMEWORKS_PATH = """c:/Users/cvanvranken/gits/MonoDevelop.Unity/Documentation/Libraries/"""
  //val FRAMEWORKS_PATH = "~/gits/MonoDevelop.Unity/Documentation/Libraries/"
  val SCRIPTREFERENCE_PATH = "/Progra~2/Unity/Editor/Data/Documentation/Documentation/ScriptReference/"
  //Seq(bash, MONODOC_PATH + "monodocer")!
  // MonoDocer Parsing Command
  //$monodoc_export_path = "export MONO_PATH=$MONOPATH:" . FRAMEWORKS_PATH . "/Mono.framework";

  //both assemblies
  //val monodoc_command = Seq(bash, "-c", mdoc("update") + FRAMEWORKS_PATH + "UnityEditor.dll " + FRAMEWORKS_PATH + "UnityEngine.dll --lib=" + FRAMEWORKS_PATH + " --out=" + SOURCE_PATH + " > " + LOG_PATH + "mdoc_update.log");
  //Editor assembly
  val monodoc_command = Seq(bash, "-c", mdoc("update") + FRAMEWORKS_PATH + "UnityEditor.dll --lib=" + FRAMEWORKS_PATH + " --out=" + SOURCE_PATH + " > " + LOG_PATH + "mdoc_update.log");
  //engine assembly
  // val monodoc_command = Seq(bash, "-c", mdoc("update") + FRAMEWORKS_PATH + "UnityEngine.dll --lib=" + FRAMEWORKS_PATH + " --out=" + SOURCE_PATH + " > " + LOG_PATH + "mdoc_update.log");
  // MonoDoc Assembler Command				
  val mdassembler_command = Seq(bash, mdoc("assemble") + " --format=ecma " + SOURCE_PATH +
    " --out " + RELEASE_PATH + "Unity > " + LOG_PATH +
    "mdoc_assemble.log");

  // MonoDoc to VS
  // val mdocexport_command = "bash " + MONODOC_PATH + "monodocs2slashdoc " + SOURCE_PATH + " --out=" + RELEASE_PATH + "Unity.xml";
  val mdocexport_command = Seq(bash, mdoc("export-msxdoc") + SOURCE_PATH + " --out=" + RELEASE_PATH + "Unity.xml");

  val monodoc_source_file = """<?xml version="1.0"?>""" +
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

  /**
   * create directory if not in existence
   */
  def mkdir(dir: String) = {
    val f = new File(dir)
    if (!f.exists) f.mkdir
  }

  def checkExists(dir: String, errorMessage: String) = {
    val f = new File(dir)
    if (!f.exists) {
      print(errorMessage)
      System.exit(1)
    }
  }

  def writeStringToNewFile(fileName: String, contents: String) = {
    val in = scala.io.Source.fromString(contents)
    val f = new File(fileName)
    f.createNewFile
    val out = new java.io.PrintWriter(f)
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
    mkdir(DOCUMENTATION_PATH)
    mkdir(SOURCE_PATH)
    mkdir(LOG_PATH)
    mkdir(RELEASE_PATH)
    mkdir(RELEASE_PATH + "MonoDevelop");
    mkdir(RELEASE_PATH + "VS");

    // Check System Locations
    checkExists(FRAMEWORKS_PATH, "Unity Framework Files Not Found")
    checkExists(SCRIPTREFERENCE_PATH, "Unity Script Reference Not Found")

    // Remove Old Logs & Misc
    new File(LOG_PATH + "mdoc_update.log").delete
    new File(LOG_PATH + "mdoc_assemble.log").delete
    new File(LOG_PATH + "updateDocumentation.log").delete
    new File(RELEASE_PATH + "Unity.source").delete

    //write out the monodoc SourceFile
    writeStringToNewFile(RELEASE_PATH + "Unity.source", monodoc_source_file.toString)
  }

}