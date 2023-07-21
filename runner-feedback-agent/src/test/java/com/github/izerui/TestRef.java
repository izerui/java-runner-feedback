//package com.github.izerui;
//
//import com.github.izerui.support.Span;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class TestRef {
//
//    public static void main(String[] args) throws IOException {
//        List<Span> spans = new ArrayList<>();
//        List<String> lines = Files.readAllLines(Path.of("/Users/serv/github/java-runner-feedback/runner-feedback-agent/src/test/resources/ref"));
//        for (String s : lines) {
//            if (s.startsWith("    ")) {
//                spans.get(spans.size() - 1).getParentIds().add(s.trim());
//            } else {
//                spans.add(Span.builder()
//                        .rootInComming(s.contains("Controller"))
//                        .id(s)
//                        .build());
//            }
//        }
//
//
////        TreeMapper<Span, Span> treeMapper = new TreeMapper<Span, Span>() {
////            @Override
////            protected boolean isRoot(Span item) {
////                return item.isRootInComming();
////            }
////
////            @Override
////            protected boolean isParent(Span child, Span parent) {
////                return Objects.equals(child.getParentId(), parent.getId());
////            }
////
////            @Override
////            protected Span map(Span item, Span parent) {
////                return item;
////            }
////
////            @Override
////            protected void addChild(Span child, Span parent) {
////                parent.getChildren().add(child);
////            }
////        };
////        List<Span> collect = treeMapper.treeMap(spans);
//
//        for (Span span : spans) {
//            if (span.isRootInComming()) {
//                continue;
//            }
//            for (String parentId : span.getParentIds()) {
//                // 找到父级调用者
//                Span parent = spans.stream()
//                        .filter(sp -> sp.getId().equals(parentId))
//                        // 作为儿子的span不能拥有parent的id，否则就是死循环
//                        .filter(sp -> !span.isContains(sp.getId()))
//                        .findFirst()
//                        .orElse(null);
//                if (parent != null) {
//                    parent.getChildren().add(span);
//                    span.setMark(1);
//                    break;
//                }
//            }
//        }
//        List<Span> collect = spans.stream().filter(span -> span.getMark() == null).collect(Collectors.toList());
//
//        for (Span span : collect) {
//            span.printTree(item -> item.getId());
//        }
//    }
//
//
//}
