package com.apkide.analysis.api.cle;

import com.apkide.analysis.api.clm.ClassType;
import com.apkide.analysis.api.clm.FileEntry;
import com.apkide.analysis.api.clm.Member;
import com.apkide.analysis.api.clm.SyntaxTree;

public interface SignatureAnalyzer {
    long getDeclarationDigest(SyntaxTree ast);

    long getPublicDeclarationDigest(SyntaxTree ast);

    long getClassDeclarationDigest(SyntaxTree ast);

    void doLoadNamespaces(FileEntry fileEntry);

    void doLoadPositions(FileEntry fileEntry);

    void doLoadTypes(FileEntry fileEntry);

    void doLoadSyntax(FileEntry fileEntry);

    void doLoadSuperClassTypes(FileEntry fileEntry, ClassType classtype);

    void doLoadDefaultSuperClassTypes(FileEntry fileEntry, ClassType classtype);

    void doLoadBoundTypes(FileEntry fileEntry, ClassType classtype);

    void doLoadSuperTypes(FileEntry fileEntry, ClassType classtype);

    void doLoadConstantValueOfField(FileEntry fileEntry, Member member);
}
