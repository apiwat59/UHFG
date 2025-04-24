package androidx.navigation;

import android.net.Uri;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
class NavDeepLink {
    private static final Pattern SCHEME_PATTERN = Pattern.compile("^[a-zA-Z]+[+\\w\\-.]*:");
    private final boolean mExactDeepLink;
    private final boolean mIsParameterizedQuery;
    private final Pattern mPattern;
    private final ArrayList<String> mArguments = new ArrayList<>();
    private final Map<String, ParamQuery> mParamArgMap = new HashMap();

    NavDeepLink(String uri) {
        Uri parameterizedUri = Uri.parse(uri);
        boolean z = parameterizedUri.getQuery() != null;
        this.mIsParameterizedQuery = z;
        StringBuilder uriRegex = new StringBuilder("^");
        if (!SCHEME_PATTERN.matcher(uri).find()) {
            uriRegex.append("http[s]?://");
        }
        Pattern fillInPattern = Pattern.compile("\\{(.+?)\\}");
        if (z) {
            Matcher matcher = Pattern.compile("(\\?)").matcher(uri);
            if (matcher.find()) {
                buildPathRegex(uri.substring(0, matcher.start()), uriRegex, fillInPattern);
            }
            this.mExactDeepLink = false;
            for (String paramName : parameterizedUri.getQueryParameterNames()) {
                StringBuilder argRegex = new StringBuilder();
                String queryParam = parameterizedUri.getQueryParameter(paramName);
                Matcher matcher2 = fillInPattern.matcher(queryParam);
                int appendPos = 0;
                ParamQuery param = new ParamQuery();
                while (matcher2.find()) {
                    param.addArgumentName(matcher2.group(1));
                    argRegex.append(Pattern.quote(queryParam.substring(appendPos, matcher2.start())));
                    argRegex.append("(.+?)?");
                    appendPos = matcher2.end();
                }
                if (appendPos < queryParam.length()) {
                    argRegex.append(Pattern.quote(queryParam.substring(appendPos)));
                }
                param.setParamRegex(argRegex.toString().replace(".*", "\\E.*\\Q"));
                this.mParamArgMap.put(paramName, param);
            }
        } else {
            this.mExactDeepLink = buildPathRegex(uri, uriRegex, fillInPattern);
        }
        String finalRegex = uriRegex.toString().replace(".*", "\\E.*\\Q");
        this.mPattern = Pattern.compile(finalRegex);
    }

    private boolean buildPathRegex(String uri, StringBuilder uriRegex, Pattern fillInPattern) {
        Matcher matcher = fillInPattern.matcher(uri);
        int appendPos = 0;
        boolean exactDeepLink = !uri.contains(".*");
        while (matcher.find()) {
            String argName = matcher.group(1);
            this.mArguments.add(argName);
            uriRegex.append(Pattern.quote(uri.substring(appendPos, matcher.start())));
            uriRegex.append("(.+?)");
            appendPos = matcher.end();
            exactDeepLink = false;
        }
        if (appendPos < uri.length()) {
            uriRegex.append(Pattern.quote(uri.substring(appendPos)));
        }
        uriRegex.append("($|(\\?(.)*))");
        return exactDeepLink;
    }

    boolean matches(Uri deepLink) {
        return this.mPattern.matcher(deepLink.toString()).matches();
    }

    boolean isExactDeepLink() {
        return this.mExactDeepLink;
    }

    Bundle getMatchingArguments(Uri deepLink, Map<String, NavArgument> arguments) {
        Map<String, NavArgument> map = arguments;
        Matcher matcher = this.mPattern.matcher(deepLink.toString());
        Bundle bundle = null;
        if (!matcher.matches()) {
            return null;
        }
        Bundle bundle2 = new Bundle();
        int size = this.mArguments.size();
        for (int index = 0; index < size; index++) {
            String argumentName = this.mArguments.get(index);
            String value = Uri.decode(matcher.group(index + 1));
            if (parseArgument(bundle2, argumentName, value, map.get(argumentName))) {
                return null;
            }
        }
        if (this.mIsParameterizedQuery) {
            for (String paramName : this.mParamArgMap.keySet()) {
                Matcher argMatcher = null;
                ParamQuery storedParam = this.mParamArgMap.get(paramName);
                String inputParams = deepLink.getQueryParameter(paramName);
                if (inputParams != null) {
                    argMatcher = Pattern.compile(storedParam.getParamRegex()).matcher(inputParams);
                    if (!argMatcher.matches()) {
                        return bundle;
                    }
                }
                int index2 = 0;
                while (index2 < storedParam.size()) {
                    String value2 = null;
                    if (argMatcher != null) {
                        value2 = Uri.decode(argMatcher.group(index2 + 1));
                    }
                    String argName = storedParam.getArgumentName(index2);
                    NavArgument argument = map.get(argName);
                    if (argument != null && (value2 == null || value2.replaceAll("[{}]", "").equals(argName))) {
                        if (argument.getDefaultValue() != null) {
                            value2 = argument.getDefaultValue().toString();
                        } else if (argument.isNullable()) {
                            value2 = null;
                        }
                    }
                    if (!parseArgument(bundle2, argName, value2, argument)) {
                        index2++;
                        bundle = null;
                        map = arguments;
                    } else {
                        return null;
                    }
                }
                map = arguments;
            }
        }
        return bundle2;
    }

    private boolean parseArgument(Bundle bundle, String name, String value, NavArgument argument) {
        if (argument != null) {
            NavType<?> type = argument.getType();
            try {
                type.parseAndPut(bundle, name, value);
                return false;
            } catch (IllegalArgumentException e) {
                return true;
            }
        }
        bundle.putString(name, value);
        return false;
    }

    private static class ParamQuery {
        private ArrayList<String> mArguments = new ArrayList<>();
        private String mParamRegex;

        ParamQuery() {
        }

        void setParamRegex(String paramRegex) {
            this.mParamRegex = paramRegex;
        }

        String getParamRegex() {
            return this.mParamRegex;
        }

        void addArgumentName(String name) {
            this.mArguments.add(name);
        }

        String getArgumentName(int index) {
            return this.mArguments.get(index);
        }

        public int size() {
            return this.mArguments.size();
        }
    }
}
