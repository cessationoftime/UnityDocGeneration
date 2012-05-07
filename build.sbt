cleanFiles <<= (cleanFiles, baseDirectory) {
(files, basedir) =>
files ++ Seq(new File(basedir, "/Documentation"))
}
