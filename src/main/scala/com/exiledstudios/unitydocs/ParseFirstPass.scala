package com.exiledstudios.unitydocs

import scala.xml.Source
import scala.io.{ Source => IOSource }
import Settings._
import scala.xml.XML
import scala.xml.Node
import sys.process.stringSeqToProcess
import scala.util.matching.Regex
case class NamespaceXML(name: String, types: Seq[TypeXML], node: Node)
case class TypeXML(name: String, kind: String, node: Node)

object ParseFirstPass {

  val unityDocStub = {
    def getName(node: Node) = (node \ "@Name").text

    val xml = XML.load(Source.fromFile(SOURCE_PATH + "index.xml"));

    (xml \ "Types" \\ "Namespace") map { ns =>
      val typ = (ns \\ "Type") map (t => TypeXML(getName(t), (t \ "@Kind").text, t))
      NamespaceXML(getName(ns), typ, ns)
    }
  }

  def updateDocumentationSource {
    for (
      namespace <- unityDocStub;
      typ <- namespace.types
    ) {
      // Because each type of file is a bit different, lets just make specific
      // calls to update each one differently.
      typ.kind match {
        case "Class" => updateDocumentationSourceDynamic(namespace.name, typ.name, "class");
        case "Enumeration" => //updateDocumentationSourceEnumeration($NamespaceObject['Name'], $TypeObject['Name']);
        case "Delegate" => println("Delegate: " + namespace.name + "." + typ.name);
        case "Structure" => //updateDocumentationSourceDynamic($NamespaceObject['Name'], $TypeObject['Name'], "structure");
        case "Interface" => println("Interface: " + namespace.name + "." + typ.name);
      }
    }
  }

  def updateForLinks(namespace: NamespaceXML, typ: TypeXML, text: String, include_type: Boolean = false) {
    /*
	global $allowed_tags;
	global $remove_links;
	global $hot_links;
	global $unity;

	while (preg_match("/<a(.*)<\/a>/U", $text, $matches))
	{
		preg_match("/class=\"(.*)\"/U", $matches[0], $matches_class);
		preg_match("/>(.*)\<\/a>/U", $matches[0], $matches_item);

		if ( in_array( $matches_item[1], $remove_links ))
		{
			$text = str_replace($matches[0], "", $text);
		} 
		
		switch ($matches_class[1])
		{
			case "itemlink":
				$tag = "P";
				break;
			case "classlink":
			default:
				$tag = "T";
				break;
		}
		
		if ( !empty($unity[strtolower($namespace)][strtolower($type)]) )
		{
			//die($namespace . $type);
			$text = str_replace($matches[0], "DOTLUNNY<see cref=\"" . $tag . ":" . $namespace . "." . 
				 $matches_item[1] . "\" />DOTGUNNY", $text);
		}
		else
		{
			$text = str_replace($matches[0], "DOTLUNNY<see cref=\"" . $tag . ":" . $namespace . "." . 
				 $type . "." . $matches_item[1] . "\" />DOTGUNNY", $text);
				
		}
	}
	
	// Remove
	foreach ( $remove_links as $key)
	{
		$text = str_ireplace("DOTLUNNY<see cref=\"T:" . $namespace . "." . $key. "\" />DOTGUNNY", "", $text);
		$text = str_ireplace("DOTLUNNY<see cref=\"P:" . $namespace . "." . $key. "\" />DOTGUNNY", "", $text);
	}
	
	// Hotlinks -- need some way to detect if they are <cref'd already>
	/*foreach ( $hot_links as $key => $url )
	{
		$text = str_ireplace($key, "DOTLUNNY<see cref=\"T:" . $url . "\" />DOTGUNNY", $text);
	}*/
	
	
	// Left overs from removing items
	$text = str_ireplace(", .", ".", $text);
	$text = str_ireplace(", ,", ",", $text);
	$text = str_ireplace(",,", ",", $text);
	$text = str_ireplace("</span>", "", $text);
	$text = str_ireplace("<span class=\"note\">", "", $text);
	$text = str_ireplace("<span class=\"variable\">", "", $text);
	$text = str_ireplace("<tt>", "", $text);
	$text = str_ireplace("</tt>", "", $text);
	
	if ( trim($text) == "See Also:  and" )
	{
		$text = "";
	}
	
	if ( trim($text) == "See Also:") 
	{
		$text = "";
	}
	
	// USE file:///Applications/Unity/Documentation/ScriptReference/EventType.html as a test bed
	/*
	    * N:MyLibrary (to link to a namespace)
	    * T:MyLibrary.MyType (to link to a type)
	    * C:MyLibrary.MyType(System.String) (to link to a constructor)
	          o Constructor links may also be written as: M:MyLibrary.MyType.#ctor(System.String) 
	    * M:MyLibrary.MyType.MethodName(System.String,MyLibray.MyOtherType) (to link to a method; for ref and out parameters, add an & to the end of the type name)
	    * P:MyLibrary.MyType.IsDefined (to link to a property)
	    * F:MyLibrary.MyType.COUNTER (to link to a field)
	    * E:MyLibrary.MyType.OnChange (to link to an event)
	*/
	

	return trim(real_strip_tags($text, $allowed_tags));
	*/
  }
  /*
  function strip_html_tags( $text )
{
    $text = preg_replace(
        array(
          // Remove invisible content
            '@<head[^>]*?>.*?</head>@siu',
            '@<style[^>]*?>.*?</style>@siu',
            '@<script[^>]*?.*?</script>@siu',
            '@<object[^>]*?.*?</object>@siu',
            '@<embed[^>]*?.*?</embed>@siu',
            '@<applet[^>]*?.*?</applet>@siu',
            '@<noframes[^>]*?.*?</noframes>@siu',
            '@<noscript[^>]*?.*?</noscript>@siu',
            '@<noembed[^>]*?.*?</noembed>@siu',
          // Add line breaks before and after blocks
            '@</?((address)|(blockquote)|(center)|(del))@iu',
            '@</?((div)|(h[1-9])|(ins)|(isindex)|(p)|(pre))@iu',
            '@</?((dir)|(dl)|(dt)|(dd)|(li)|(menu)|(ol)|(ul))@iu',
            '@</?((table)|(th)|(td)|(caption))@iu',
            '@</?((form)|(button)|(fieldset)|(legend)|(input))@iu',
            '@</?((label)|(select)|(optgroup)|(option)|(textarea))@iu',
            '@</?((frameset)|(frame)|(iframe))@iu',
        ),
        array(
            ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',"$0", "$0", "$0", "$0", "$0", "$0","$0", "$0",), $text );
  
    // you can exclude some html tags here, in this case B and A tags        
    return strip_tags( $text , '<b><a>' );
}
 */
  def strip_html_tags(text: String, skipTags: List[String]) = {

    //the list of tags to remove
    val tags = List("head", "style", "script", "object", "embed", "applet", "noframes", "noscript", "noembed")
    //the list of tags minus the ones we want to keep
    val tagsToStrip = tags filterNot (skipTags contains _)

    //the regex to use to remove a tag
    def findTag(tag: String) = ("(?siu)<" + tag + "[^>]*?.*?</" + tag + ">").r

    //build the collection of regex for the tags
    val regexForStripping = tagsToStrip map { findTag _ }

    //filter the text with the collection of regex's
    regexForStripping.foldLeft(text)((t, r) => r.replaceAllIn(t, m => ""))

  }

  def scrubFile(f: String) = {
    val f2 = f.replace("""\n""", "")

    //  (Seq("php", "print strip_tags(\"" + f + "\", '<p><span><a><h3>');")!!).trim
    //strip_tags($file, '<p><span><a><h3>');
    strip_html_tags(f2, List("p", "span", "a", "h3"))
  }

  def updateDocumentationSourceDynamic(namespace: String, typ: String, doc_type: String = "class") {
    //global $warnings;
    //global $errors;

    val xml = XML.load(Source.fromFile(SOURCE_PATH + namespace + "/" + typ + ".xml"));
    //$objectXML = simplexml_load_file(SOURCE_PATH . $namespace . "/" . $type . ".xml");

    val sourceHtml = {
      val s = scala.io.Source.fromFile(SCRIPTREFERENCE_PATH + typ + ".html")
      val htmllines = s.mkString
      s.close()
      scrubFile(htmllines)
    }

    // Find base summary
    //$file = scrubFile(@file_get_contents(SCRIPTREFERENCE_PATH . $type . ".html", "r"));
    /*

	if (!sourceHtml.isEmpty)
	{
		if(preg_match("/<p class=\"first\">(.*)<\/p>/U", $file, $matches))
		{
			$updated_text = updateForLinks($namespace, $type, $matches[1]);
			if ( !empty($updated_text) )
			{
				$objectXML->Docs->summary = $updated_text;
			}
		}
		else
		{
			$warnings[$doc_type]++;
			if ( SHOW_WARNINGS )
			{
				file_put_contents(LOG_PATH . "updateDocumentation.log", "WARNING: No " . $doc_type ." summary in " . 
					SCRIPTREFERENCE_PATH . $type . ".html" . "\n", FILE_APPEND | LOCK_EX);
			}
		}
		
		$matches = null;
		if (preg_match("/<span class=\"note\">(.*)<\/p>/U", $file, $matches))
		{
			$updated_text = updateForLinks($namespace, $type, $matches[1]);
			if ( !empty($updated_text) )
			{		
				$objectXML->Docs->remarks = $updated_text;
			}
		}
	}
	else
	{
		$errors[$doc_type]++;
		file_put_contents(LOG_PATH . "updateDocumentation.log", "ERROR: No " . $doc_type . " file  found (" . 
			SCRIPTREFERENCE_PATH . $type . ".html)" . "\n", FILE_APPEND | LOCK_EX);
	}
	$file = null;


	// Fill out all the enumeration types and fill out their information
	foreach ($objectXML->Members->Member as $MemberObject)
	{
		switch ($MemberObject->MemberType)
		{
			case "Constructor":
				$file_name = $type . "." . $type . ".html";
				$file_type = $doc_type . " Contructor";
				break;
				
			case "Method":
				$file_name = $type . "." . $MemberObject["MemberName"] . ".html";
				
				if ( substr($MemberObject["MemberName"], 0, 3) == "op_" )
				{
					$file_name = $type . "-" . str_replace("op_", "operator_", $MemberObject["MemberName"]) . ".html";
				}
				
				// Special Cases
				$file_name = str_replace("_Addition.html", "_add.html", $file_name);
				$file_name = str_replace("_Subtraction.html", "_subtract.html", $file_name);
				$file_name = str_replace("_Division.html", "_divide.html", $file_name);
				$file_name = str_replace("_Multiply.html", "_multiply.html", $file_name);
											
				$file_type = $doc_type . " Method";
				break;
				
			case "Field":
				$file_name = $type . "-" . $MemberObject["MemberName"] . ".html";
				$file_type = $doc_type . " Field";
				break;
				
			case "Property":
				$file_name = $type . "-" . $MemberObject["MemberName"] . ".html";
				$file_type = $doc_type . " Property";
				break;
				
			default: 
				die("No definition for " . $namespace . "." . $type . "." 
					. $MemberObject['MemberName'] . "->" . $MemberObject->MemberType . "\n");
		}
		
		// Load File
		$file = scrubFile(@file_get_contents(SCRIPTREFERENCE_PATH . $file_name, "r"));	
	
	
		// No joy, no luv
		if(empty($file)) 
		{
			$errors[$doc_type]++;
			file_put_contents(LOG_PATH . "updateDocumentation.log", "ERROR: No " . $file_type . " file found (" . 
				SCRIPTREFERENCE_PATH . $file_name . "\n", FILE_APPEND | LOCK_EX); 
			continue; 
		}		
		
		
		switch ($MemberObject->MemberType)
		{
			
			case "Method":
			
				// Do we have a description field for Summary
				if ( $MemberObject->Docs->summary == DOC_EMPTY || DOC_OVERWRITE)
				{
					if (stristr($file,'<h3 class="soft">Description</h3>'))
					{
						$matches = null;
						if (preg_match("/<p class=\"details\">(.*)<\/p>/U", substr($file, 
								strpos($file,'<h3 class="soft">Description</h3>') + 
								strlen('<h3 class="soft">Description</h3>')), $matches))
						{
							$updated_text = updateForLinks($namespace, $type, $matches[1], true);
							if ( !empty($updated_text))
							{
								$MemberObject->Docs->summary = $updated_text;
							}
						}
					}
					else
					{
						// Fail Safe Method 
						
						// Get first <p> details for the description
						$matches = null;
						if(preg_match("/<p class=\"details\">(.*)<\/p>/U", $file, $matches))
						{
							$updated_text = updateForLinks($namespace, $type, $matches[1], true);
							if ( !empty($updated_text) )
							{
								$MemberObject->Docs->summary = $updated_text;
							}
						}
						else
						{
							$warnings[$doc_type]++;
							if ( SHOW_WARNINGS )
							{
								file_put_contents(LOG_PATH . "updateDocumentation.log", "WARNING: No " . $file_type . " summary in " . 
									SCRIPTREFERENCE_PATH . $file_name . "\n", FILE_APPEND | LOCK_EX);
							}
						}	
					}
				}
				
				// Do we have a return field
				if (($MemberObject->Docs->returns == DOC_EMPTY || DOC_OVERWRITE) &&
				  	stristr($file, '<h3 class="soft">Returns</h3>'))
				{
					$matches = null;
					if (preg_match("/<p class=\"details\">(.*)<\/p>/U", substr($file, strpos($file,'<h3 class="soft">Returns</h3>') 
						+ strlen('<h3 class="soft">Returns</h3>')), $matches))
					{
						$updated_text = updateForLinks($namespace, $type, $matches[1], true);
						if ( !empty($updated_text))
						{
							$MemberObject->Docs->returns = $updated_text;
						}
					}
				}
				
				break;
			case "Constructor":
			case "Field":
			case "Property":
			default:
				if ( $MemberObject->Docs->summary == DOC_EMPTY || DOC_OVERWRITE)
				{			
				
					// Get first <p> details for the description
					$matches = null;
					if(preg_match("/<p class=\"details\">(.*)<\/p>/U", $file, $matches))
					{
						$updated_text = updateForLinks($namespace, $type, $matches[1], true);
						if ( !empty($updated_text) )
						{
							$MemberObject->Docs->summary = $updated_text;
						}
					}
					else
					{
						$warnings[$doc_type]++;
						if ( SHOW_WARNINGS )
						{
							file_put_contents(LOG_PATH . "updateDocumentation.log", "WARNING: No " . $file_type . " summary in " . 
								SCRIPTREFERENCE_PATH . $file_name . "\n", FILE_APPEND | LOCK_EX);
						}
					}	
				}
				if ( $MemberObject->Docs->remarks == DOC_EMPTY || DOC_OVERWRITE )
				{
					$matches = null;
					if (preg_match("/<span class=\"note\">(.*)<\/p>/U", $file, $matches))
					{
						$updated_text = updateForLinks($namespace, $type, $matches[1], true);
						if ( !empty($updated_text) )
						{
							$MemberObject->Docs->remarks = $updated_text;
						}
					}
				}
				break;
		}		
	}
	
	// Save File
	file_put_contents(SOURCE_PATH . $namespace . "/" . $type . ".xml",  simpleXMLHack(trim($objectXML->asXML())));
*/
  }
  /*
function updateDocumentationSourceEnumeration($namespace, $type)
{
	global $errors;
	global $warnings;
	
	$objectXML = simplexml_load_file(SOURCE_PATH . $namespace . "/" . $type . ".xml");
	
	// Find base summary
	$file = scrubFile(@file_get_contents(SCRIPTREFERENCE_PATH . $type . ".html", "r"));
	if (!empty($file))
	{
		if(preg_match("/<p class=\"first\">(.*)<\/p>/U", $file, $matches))
		{
			$updated_text = updateForLinks($namespace, $type, $matches[1]);
			if ( !empty($updated_text) )
			{
				$objectXML->Docs->summary = $updated_text;
			}
		}
		else
		{
			$warnings['enumeration']++;
			if ( SHOW_WARNINGS )
			{
				file_put_contents(LOG_PATH . "updateDocumentation.log", "WARNING: No enumeration summary in " . 
					SCRIPTREFERENCE_PATH . $type . ".html" . "\n", FILE_APPEND | LOCK_EX);
			}
		}
		$matches = null;
		if (preg_match("/<span class=\"note\">(.*)<\/p>/U", $file, $matches))
		{
			$updated_text = updateForLinks($namespace, $type, $matches[1]);
			if ( !empty($updated_text) )
			{
				$objectXML->Docs->remarks = $updated_text;
			}		
		}
	}
	else
	{
		$errors['enumeration']++;
		file_put_contents(LOG_PATH . "updateDocumentation.log", "ERROR: No enumeration file  found (" . 
			SCRIPTREFERENCE_PATH . $type . ".html)" . "\n", FILE_APPEND | LOCK_EX);
	}
	
	// Make sure we have nothing kickin' around
	$file = null;
	$matches = null;
	
	
	// Fill out all the enumeration types and fill out their information
	foreach ($objectXML->Members->Member as $MemberObject)
	{		
		if ( $MemberObject->Docs->summary == DOC_EMPTY || DOC_OVERWRITE)
		{			
			// Open Documentation File
			$file = scrubFile(@file_get_contents(SCRIPTREFERENCE_PATH . $type . "." . $MemberObject["MemberName"] . ".html", "r"));
			
			// No joy, no luv
			if(empty($file)) 
			{
				$errors['enumeration']++;
				file_put_contents(LOG_PATH . "updateDocumentation.log", "ERROR: No enumeration member file found (" . 
					SCRIPTREFERENCE_PATH . $type . "." . $MemberObject["MemberName"] . ".html)" . "\n", FILE_APPEND | LOCK_EX); 
				continue; 
			}
						
			// Strip Newlines
			$file = str_replace("\n", "", $file);
			
			// Get first <p> details for the description
			if(preg_match("/<p class=\"details\">(.*)<\/p>/U", $file, $matches))
			{
				$updated_text = updateForLinks($namespace, $type, $matches[1]);
				if ( !empty($updated_text) )
				{
					$MemberObject->Docs->summary = $updated_text;
				}
			}
			else
			{
				$errors['enumeration']++;
				if ( SHOW_WARNINGS )
				{
					file_put_contents(LOG_PATH . "updateDocumentation.log", "WARNING: No enumaration member summary in " . 
						SCRIPTREFERENCE_PATH . $type . "." . $MemberObject["MemberName"] . ".html" . "\n", FILE_APPEND | LOCK_EX);
				}
			}	
			
			$matches = null;
			if (preg_match("/<span class=\"note\">(.*)<\/p>/U", $file, $matches))
			{
				$updated_text = updateForLinks($namespace, $type, $matches[1]);
				if ( !empty($updated_text) )
				{
					$MemberObject->Docs->remarks = $updated_text;
				}
			}

		}
	}

	// Save File
	file_put_contents(SOURCE_PATH . $namespace . "/" . $type . ".xml", simpleXMLHack(trim($objectXML->asXML())));
}

  */
}