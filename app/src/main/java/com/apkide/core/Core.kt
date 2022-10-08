package com.apkide.core

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import com.apkide.core.androlib.util.BrutIO
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

@SuppressLint("StaticFieldLeak")
object Core {
	var context: Context? = null
	@JvmStatic
	@Throws(IOException::class)
	fun fileAsInputStream(file: File?): InputStream {
		return FileInputStream(file)
	}
	
	@Throws(IOException::class)
	fun openAssets(path: String?): InputStream {
		return context!!.assets.open(path)
	}
	
	@Throws(IOException::class)
	fun openAssets(
		path: String,
		executable: Boolean,
		arch: String,
		compatibleArch: String
	): InputStream {
		if (executable) {
			val inputStream: InputStream = try {
				openAssets(arch + File.separator + path)
			} catch (e: IOException) {
				return openAssets(compatibleArch + File.separator + path)
			}
			return inputStream
		}
		return openAssets(path)
	}
	
	@get:Throws(IllegalArgumentException::class)
	val compatibleArch: Array<String>
		get() {
			val array = Build.SUPPORTED_ABIS
			for (arch in array) {
				when (arch) {
					"arm64-v8a" -> return arrayOf("arm64", "arm-pie")
					"armeabi-v7a" -> return arrayOf("arm-pie")
					"x86" -> return arrayOf("x86", "arm-pie")
					"x86_64" -> return arrayOf("x86-pie", "x86")
				}
			}
			throw IllegalArgumentException("The cpu architecture of the current device is not yet supported．")
		}
	
	fun extract(inputStream: InputStream?, outputStream: OutputStream?) {
		try {
			BrutIO.copyAndClose(inputStream, outputStream)
		} catch (e: IOException) {
			e.printStackTrace()
		}
	}
	
	@Throws(IOException::class)
	fun extractExecutable(executable: String, target: File) {
		val abis = compatibleArch
		extractExecutable(executable, abis[0], if (abis.size == 2) abis[1] else abis[0], target)
	}
	
	@Throws(IOException::class)
	private fun extractExecutable(
		executable: String,
		arch: String,
		compatibleArch: String,
		target: File
	) {
		val inputStream = openAssets(executable, true, arch, compatibleArch)
		try {
			if (!target.exists()) {
				target.createNewFile()
			}
			extract(inputStream, FileOutputStream(target))
		} catch (e: IOException) {
			e.printStackTrace()
		}
		if (!target.setExecutable(true)) target.setExecutable(true, true)
	}
	
	@Throws(IOException::class)
	fun getExecutableFilePath(executable: String): String {
		return getExecutableFile(executable).absolutePath
	}
	
	@JvmStatic
	@Throws(IOException::class)
	fun getExecutableFile(executable: String): File {
		val file = File(executableFileDir, executable)
		if (!file.exists() || file.isDirectory) extractExecutable(executable, file)
		return file
	}
	
	val executableFileDir: File
		get() = mkdirIfNot(File(context!!.filesDir, "bin"))
	@JvmStatic
	val frameworkFileDir: File
		get() = mkdirIfNot(File(context!!.filesDir, "framework"))
	
	@JvmStatic
	@get:Throws(IOException::class)
	val androidFrameworkFile: File
		get() {
			val file = File(frameworkFileDir, "android-framework.jar")
			if (!file.exists() || file.isDirectory) {
				if (!file.exists()) file.createNewFile()
				extract(openAssets("android-framework.jar"), FileOutputStream(file))
			}
			return file
		}
	
	@JvmStatic
	fun mkdirIfNot(file: File): File {
		if (!file.exists() || file.isFile) file.mkdirs()
		return file
	}
}