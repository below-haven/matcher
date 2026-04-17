package matcher.cli.provider.builtin;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import matcher.cli.MatcherCli;
import matcher.cli.provider.CliCommandProvider;
import matcher.core.Matcher;
import matcher.core.serdes.MatchesIo;
import matcher.model.InputFile;
import matcher.model.config.Config;
import matcher.model.config.ProjectConfig;
import matcher.model.type.ClassEnvironment;

/**
 * Provides the default {@code automatch} command.
 */
public class AutomatchCliCommandProvider implements CliCommandProvider {
	@Parameters(commandNames = {commandName})
	class AutomatchCommand {
		@Parameter(names = {BuiltinCliParameters.INPUTS_A}, required = true)
		List<Path> inputsA = Collections.emptyList();

		@Parameter(names = {BuiltinCliParameters.INPUTS_B}, required = true)
		List<Path> inputsB = Collections.emptyList();

		@Parameter(names = {BuiltinCliParameters.CLASSPATH_A})
		List<Path> classpathA = Collections.emptyList();

		@Parameter(names = {BuiltinCliParameters.CLASSPATH_B})
		List<Path> classpathB = Collections.emptyList();

		@Parameter(names = {BuiltinCliParameters.SHARED_CLASSPATH})
		List<Path> sharedClasspath = Collections.emptyList();

		@Parameter(names = {BuiltinCliParameters.INPUTS_BEFORE_CLASSPATH})
		boolean inputsBeforeClasspath;

		@Parameter(names = {BuiltinCliParameters.NON_OBFUSCATED_CLASS_PATTERN_A})
		String nonObfuscatedClassPatternA = "";

		@Parameter(names = {BuiltinCliParameters.NON_OBFUSCATED_CLASS_PATTERN_B})
		String nonObfuscatedClassPatternB = "";

		@Parameter(names = {BuiltinCliParameters.NON_OBFUSCATED_MEMBER_PATTERN_A})
		String nonObfuscatedMemberPatternA = "";

		@Parameter(names = {BuiltinCliParameters.NON_OBFUSCATED_MEMBER_PATTERN_B})
		String nonObfuscatedMemberPatternB = "";

		@Parameter(names = {BuiltinCliParameters.MAPPINGS_A})
		Path mappingsPathA;

		@Parameter(names = {BuiltinCliParameters.MAPPINGS_B})
		Path mappingsPathB;

		@Parameter(names = {BuiltinCliParameters.OUTPUT_FILE}, required = true)
		Path outputFile;

		@Parameter(names = {BuiltinCliParameters.DONT_SAVE_UNMAPPED_MATCHES})
		boolean dontSaveUnmappedMatches;

		@Parameter(names = {BuiltinCliParameters.PASSES})
		int passes = 1;
	}

	@Override
	public String getCommandName() {
		return commandName;
	}

	@Override
	public Object getDataHolder() {
		return command;
	}

	@Override
	public void processArgs() {
		Matcher.init();
		ClassEnvironment env = new ClassEnvironment();
		Matcher matcher = new Matcher(env);
		ProjectConfig config = new ProjectConfig.Builder(InputFile.fromPaths(command.inputsA), InputFile.fromPaths(command.inputsB))
				.classPathA(InputFile.fromPaths(command.classpathA))
				.classPathB(InputFile.fromPaths(command.classpathB))
				.sharedClassPath(InputFile.fromPaths(command.sharedClasspath))
				.inputsBeforeClassPath(command.inputsBeforeClasspath)
				.mappingsPathA(command.mappingsPathA)
				.mappingsPathB(command.mappingsPathB)
				.saveUnmappedMatches(!command.dontSaveUnmappedMatches)
				.nonObfuscatedClassPatternA(command.nonObfuscatedClassPatternA)
				.nonObfuscatedClassPatternB(command.nonObfuscatedClassPatternB)
				.nonObfuscatedMemberPatternA(command.nonObfuscatedMemberPatternA)
				.nonObfuscatedMemberPatternB(command.nonObfuscatedMemberPatternB)
				.build();

		Config.setProjectConfig(config);
		matcher.init(config, (progress) -> { });

		for (int i = 0; i < command.passes; i++) {
			matcher.autoMatchAll((progress) -> { });
		}

		try {
			Files.deleteIfExists(command.outputFile);
			MatchesIo.write(matcher, command.outputFile);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}

		MatcherCli.LOGGER.info("Auto-matching done!");
	}

	private static final String commandName = "automatch";
	private final AutomatchCommand command = new AutomatchCommand();
}
